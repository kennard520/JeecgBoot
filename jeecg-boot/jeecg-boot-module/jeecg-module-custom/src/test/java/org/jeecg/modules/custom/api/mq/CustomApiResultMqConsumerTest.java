package org.jeecg.modules.custom.api.mq;

import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomApiResultMqConsumerTest {

    @Test
    void malformedMessageEscapesToBoundedContainerRetryAndDlq() throws Exception {
        CustomApiResultMqConsumer consumer = new CustomApiResultMqConsumer();
        Message malformed = new Message("not-json".getBytes(StandardCharsets.UTF_8),
                new MessageProperties());

        assertThatThrownBy(() -> consumer.onMessage(malformed))
                .isInstanceOf(Exception.class);

        Method listenerMethod = CustomApiResultMqConsumer.class.getMethod("onMessage", Message.class);
        RabbitListener listener = listenerMethod.getAnnotation(RabbitListener.class);
        assertThat(listener).isNotNull();
        assertThat(listener.containerFactory()).isEqualTo("customApiResultListenerContainerFactory");
    }

    @Test
    void resultQueueDeadLettersRejectedMessages() {
        CustomApiRabbitConfig config = new CustomApiRabbitConfig();

        Queue resultQueue = config.customApiParseResultQueue();
        Queue deadLetterQueue = config.customApiParseResultDeadLetterQueue();

        assertThat(resultQueue.getArguments())
                .containsEntry("x-dead-letter-exchange", CustomApiMqConstant.PARSE_RESULT_DLX)
                .containsEntry("x-dead-letter-routing-key", CustomApiMqConstant.PARSE_RESULT_DLQ_ROUTING_KEY);
        assertThat(deadLetterQueue.getName()).isEqualTo(CustomApiMqConstant.PARSE_RESULT_DLQ);
        assertThat(deadLetterQueue.isDurable()).isTrue();
    }
}
