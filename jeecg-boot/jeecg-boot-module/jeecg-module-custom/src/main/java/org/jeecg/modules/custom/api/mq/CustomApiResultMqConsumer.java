package org.jeecg.modules.custom.api.mq;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.jeecg.modules.custom.api.service.ICustomApiTaskService;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class CustomApiResultMqConsumer {
    private final ObjectMapper mapper = JsonMapper.builder().build();

    @Autowired
    private ICustomApiTaskService taskService;

    @RabbitListener(queues = CustomApiMqConstant.PARSE_RESULT_QUEUE,
            containerFactory = "customApiResultListenerContainerFactory")
    public void onMessage(Message message) throws Exception {
        Map<String, Object> body = mapper.readValue(
                message.getBody(), new TypeReference<Map<String, Object>>() {});
        log.info("Custom API parse result received, taskId={}, status={}",
                body.get("taskId"), body.get("status"));
        taskService.handleParseResult(body);
    }
}
