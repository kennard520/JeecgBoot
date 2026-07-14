package org.jeecg.modules.custom.api.mq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.service.ICustomApiTaskService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CustomApiResultMqConsumer {
    private final ObjectMapper mapper = JsonMapper.builder().build();
    private final ICustomApiTaskService taskService;
    private final CustomResultRetryPublisher retryPublisher;
    private final int shortAttempts;
    private final long shortBackoffMillis;

    public CustomApiResultMqConsumer(
            ICustomApiTaskService taskService,
            CustomResultRetryPublisher retryPublisher,
            @Value("${custom.api.result-consumer.short-attempts:3}") int shortAttempts,
            @Value("${custom.api.result-consumer.short-backoff-ms:1000}") long shortBackoffMillis) {
        this.taskService = taskService;
        this.retryPublisher = retryPublisher;
        this.shortAttempts = Math.max(1, shortAttempts);
        this.shortBackoffMillis = Math.max(0L, shortBackoffMillis);
    }

    @RabbitListener(queues = CustomApiMqConstant.PARSE_RESULT_QUEUE,
            containerFactory = "customApiResultListenerContainerFactory")
    public void onMessage(Message message, Channel channel) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        Exception failure = processWithShortRetries(message);
        if (failure == null) {
            channel.basicAck(deliveryTag, false);
            return;
        }

        int retryAttempt = retryAttempt(message);
        if (retryAttempt >= 3) {
            log.error("Custom API parse result exhausted delayed retries; rejecting to policy DLQ", failure);
            channel.basicNack(deliveryTag, false, false);
            return;
        }
        try {
            retryPublisher.publish(message, retryAttempt + 1);
            channel.basicAck(deliveryTag, false);
        } catch (Exception publishFailure) {
            log.error("Custom API parse result retry publish failed; broker will redeliver", publishFailure);
            channel.basicNack(deliveryTag, false, true);
        }
    }

    private Exception processWithShortRetries(Message message) {
        Exception lastFailure = null;
        for (int attempt = 1; attempt <= shortAttempts; attempt++) {
            try {
                Map<String, Object> body = mapper.readValue(
                        message.getBody(), new TypeReference<Map<String, Object>>() {});
                log.info("Custom API parse result received, taskId={}, status={}, attempt={}",
                        body.get("taskId"), body.get("status"), attempt);
                taskService.handleParseResult(body);
                return null;
            } catch (Exception e) {
                lastFailure = e;
                if (attempt < shortAttempts && !sleepBeforeRetry()) {
                    return new IllegalStateException("result retry interrupted", e);
                }
            }
        }
        return lastFailure;
    }

    private boolean sleepBeforeRetry() {
        if (shortBackoffMillis == 0L) {
            return true;
        }
        try {
            Thread.sleep(shortBackoffMillis);
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }

    private int retryAttempt(Message message) {
        Object value = message.getMessageProperties()
                .getHeader(CustomApiMqConstant.PARSE_RESULT_RETRY_HEADER);
        if (value instanceof Number number) {
            return Math.max(0, number.intValue());
        }
        try {
            return value == null ? 0 : Math.max(0, Integer.parseInt(value.toString()));
        } catch (NumberFormatException ignored) {
            return 0;
        }
    }
}
