package org.jeecg.modules.custom.api.mq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.jeecg.common.exception.JeecgBootException;
import org.jeecg.modules.custom.api.entity.CustomApiFile;
import org.jeecg.modules.custom.api.entity.CustomApiTask;
import org.jeecg.modules.custom.api.storage.ObjectStorageService;
import org.jeecg.modules.custom.api.vo.FileDownloadInfo;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class CustomApiTaskMqProducer {
    private final ObjectMapper mapper = JsonMapper.builder().build();

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private ObjectStorageService objectStorageService;

    public void sendParseTask(CustomApiTask task, CustomApiFile file) {
        String routingKey = task.getCompanyCode();
        if (routingKey == null || routingKey.isBlank()) {
            throw new JeecgBootException("companyCode is required for MQ routing");
        }
        try {
            ensureRequestQueue(routingKey);
            FileDownloadInfo download = objectStorageService.createDownloadUrl(file, LocalDateTime.now().plusHours(2));
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("taskId", task.getTaskId());
            body.put("fileId", task.getFileId());
            body.put("customerCode", task.getCustomerCode());
            body.put("clientTaskId", task.getClientTaskId());
            body.put("companyCode", task.getCompanyCode());
            body.put("direction", task.getDirection());
            body.put("originalFilename", file.getOriginalFilename());
            body.put("contentType", file.getContentType());
            body.put("fileSize", file.getFileSize());
            body.put("downloadUrl", download.getUrl());
            body.put("downloadHeaders", download.getHeaders());
            body.put("attemptNo", 1);
            body.put("maxAttempts", 3);
            body.put("metadata", task.getMetadataJson());

            Message message = MessageBuilder.withBody(mapper.writeValueAsBytes(body))
                    .setContentType(MessageProperties.CONTENT_TYPE_JSON)
                    .setDeliveryMode(MessageDeliveryMode.PERSISTENT)
                    .build();
            rabbitTemplate.send(CustomApiMqConstant.PARSE_REQUEST_EXCHANGE, routingKey, message);
        } catch (JeecgBootException e) {
            throw e;
        } catch (Exception e) {
            throw new JeecgBootException("send parse task MQ failed: " + e.getMessage());
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
