package org.jeecg.modules.custom.api.mq;

import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.entity.CustomMqOutbox;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

@Component
public class CustomApiTaskMqProducer {
    private final RabbitTemplate rabbitTemplate;
    private final AmqpAdmin amqpAdmin;
    private final long confirmTimeoutMillis;

    @Autowired
    public CustomApiTaskMqProducer(
            RabbitTemplate rabbitTemplate,
            AmqpAdmin amqpAdmin,
            @Value("${custom.api.outbox.confirm-timeout-ms:10000}") long confirmTimeoutMillis) {
        this.rabbitTemplate = rabbitTemplate;
        this.amqpAdmin = amqpAdmin;
        this.confirmTimeoutMillis = Math.max(1L, confirmTimeoutMillis);
    }

    public void publishConfirmed(CustomApiTask task, CustomApiFile file, CustomMqOutbox event) {
        validate(task, file, event);
        String routingKey = event.getRoutingKey();
        try {
            ensureRequestQueue(routingKey);
            Message message = MessageBuilder
                    .withBody(event.getPayloadJson().getBytes(StandardCharsets.UTF_8))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setMessageId(event.getEventId())
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            CorrelationData correlation = new CorrelationData(event.getEventId());
            rabbitTemplate.send(event.getExchangeName(), routingKey, message, correlation);
            CorrelationData.Confirm confirm = correlation.getFuture()
                    .get(confirmTimeoutMillis, TimeUnit.MILLISECONDS);
            if (!confirm.isAck()) {
                throw new JeecgBootException("RabbitMQ publisher confirm failed: " + confirm.getReason());
            }
            if (correlation.getReturned() != null) {
                throw new JeecgBootException("RabbitMQ returned unroutable message: "
                        + correlation.getReturned().getReplyText());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new JeecgBootException("RabbitMQ publisher confirm interrupted");
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new JeecgBootException("RabbitMQ publisher confirm failed: " + e.getMessage());
        }
    }

    private void validate(CustomApiTask task, CustomApiFile file, CustomMqOutbox event) {
        if (task == null || file == null || event == null) {
            throw new JeecgBootException("task, file and outbox event are required");
        }
        if (event.getEventId() == null || event.getEventId().isBlank()
                || event.getPayloadJson() == null || event.getPayloadJson().isBlank()) {
            throw new JeecgBootException("outbox event id and payload are required");
        }
        if (event.getExchangeName() == null || event.getExchangeName().isBlank()
                || event.getRoutingKey() == null || event.getRoutingKey().isBlank()) {
            throw new JeecgBootException("outbox exchange and routing key are required");
        }
        if (!event.getRoutingKey().equals(task.getCompanyCode())) {
            throw new JeecgBootException("outbox routing key does not match task agent");
        }
        if (!file.getFileId().equals(task.getFileId())
                || !CustomApiFile.STATUS_UPLOADED.equals(file.getStatus())) {
            throw new JeecgBootException("outbox file does not match an uploaded task file");
        }
    }

    private void ensureRequestQueue(String routingKey) {
        DirectExchange exchange = new DirectExchange(CustomApiMqConstant.PARSE_REQUEST_EXCHANGE, true, false);
        Queue queue = QueueBuilder.durable(CustomApiMqConstant.PARSE_REQUEST_QUEUE_PREFIX + routingKey).build();
        amqpAdmin.declareExchange(exchange);
        amqpAdmin.declareQueue(queue);
        amqpAdmin.declareBinding(BindingBuilder.bind(queue).to(exchange).with(routingKey));
    }
}
