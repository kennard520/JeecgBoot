package org.jeecg.modules.custom.api.mq;

import org.jeecg.common.exception.JeecgBootException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Component
public class RabbitCustomResultRetryPublisher implements CustomResultRetryPublisher {
    private final RabbitTemplate rabbitTemplate;
    private final long confirmTimeoutMillis;

    public RabbitCustomResultRetryPublisher(
            RabbitTemplate rabbitTemplate,
            @Value("${custom.api.result-consumer.confirm-timeout-ms:10000}") long confirmTimeoutMillis) {
        this.rabbitTemplate = rabbitTemplate;
        this.confirmTimeoutMillis = Math.max(1L, confirmTimeoutMillis);
    }

    @Override
    public void publish(Message source, int retryAttempt) {
        String routingKey = routingKey(retryAttempt);
        MessageProperties properties = copyProperties(source.getMessageProperties());
        properties.setDeliveryMode(MessageDeliveryMode.PERSISTENT);
        properties.setHeader(CustomApiMqConstant.PARSE_RESULT_RETRY_HEADER, retryAttempt);
        Message retryMessage = new Message(source.getBody(), properties);
        CorrelationData correlation = new CorrelationData(UUID.randomUUID().toString());
        try {
            rabbitTemplate.send(CustomApiMqConstant.PARSE_RESULT_RETRY_EXCHANGE,
                    routingKey, retryMessage, correlation);
            CorrelationData.Confirm confirm = correlation.getFuture()
                    .get(confirmTimeoutMillis, TimeUnit.MILLISECONDS);
            if (!confirm.isAck()) {
                throw new JeecgBootException("RabbitMQ retry publisher confirm nack: " + confirm.getReason());
            }
            if (correlation.getReturned() != null) {
                throw new JeecgBootException("RabbitMQ retry message was unroutable: "
                        + correlation.getReturned().getReplyText());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JeecgBootException("RabbitMQ retry publisher confirm interrupted");
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new JeecgBootException("RabbitMQ retry publisher confirm failed: " + e.getMessage());
        }
    }

    private String routingKey(int retryAttempt) {
        return switch (retryAttempt) {
            case 1 -> CustomApiMqConstant.PARSE_RESULT_RETRY_60_ROUTING_KEY;
            case 2 -> CustomApiMqConstant.PARSE_RESULT_RETRY_300_ROUTING_KEY;
            case 3 -> CustomApiMqConstant.PARSE_RESULT_RETRY_900_ROUTING_KEY;
            default -> throw new JeecgBootException("unsupported result retry attempt: " + retryAttempt);
        };
    }

    private MessageProperties copyProperties(MessageProperties source) {
        MessageProperties target = new MessageProperties();
        target.getHeaders().putAll(source.getHeaders());
        target.setContentType(source.getContentType());
        target.setContentEncoding(source.getContentEncoding());
        target.setMessageId(source.getMessageId());
        target.setType(source.getType());
        target.setAppId(source.getAppId());
        return target;
    }
}
