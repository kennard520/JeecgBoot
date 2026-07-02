package org.jeecg.modules.custom.api.mq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.service.ICustomApiTaskService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CustomApiResultMqConsumer {
    private final ObjectMapper mapper = JsonMapper.builder().build();

    @Autowired
    private ICustomApiTaskService taskService;

    @RabbitListener(queues = CustomApiMqConstant.PARSE_RESULT_QUEUE)
    public void onMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long deliveryTag)
            throws Exception {
        try {
            Map<String, Object> body = mapper.readValue(message.getBody(), new TypeReference<Map<String, Object>>() {});
            log.info("Custom API parse result received, taskId={}, status={}", body.get("taskId"), body.get("status"));
            taskService.handleParseResult(body);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("Custom API parse result handle failed", e);
            channel.basicNack(deliveryTag, false, true);
        }
    }
}
