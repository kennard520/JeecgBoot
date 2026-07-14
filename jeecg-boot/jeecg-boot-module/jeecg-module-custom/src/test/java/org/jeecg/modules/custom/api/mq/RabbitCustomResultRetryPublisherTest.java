package org.jeecg.modules.custom.api.mq;

import org.jeecg.common.exception.JeecgBootException;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class RabbitCustomResultRetryPublisherTest {

    @Test
    void publishesPersistentCopyToSelectedDelayQueueAndWaitsForConfirm() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        RabbitCustomResultRetryPublisher publisher =
                new RabbitCustomResultRetryPublisher(rabbit, 1000L);
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(true, null));
            return null;
        }).when(rabbit).send(any(String.class), any(String.class),
                any(Message.class), any(CorrelationData.class));
        Message source = new Message("payload".getBytes(), new MessageProperties());

        publisher.publish(source, 2);

        ArgumentCaptor<Message> sent = ArgumentCaptor.forClass(Message.class);
        verify(rabbit).send(eq(CustomApiMqConstant.PARSE_RESULT_RETRY_EXCHANGE),
                eq(CustomApiMqConstant.PARSE_RESULT_RETRY_300_ROUTING_KEY),
                sent.capture(), any(CorrelationData.class));
        assertThat(sent.getValue().getBody()).isEqualTo(source.getBody());
        assertThat(sent.getValue().getMessageProperties().getDeliveryMode())
                .isEqualTo(MessageDeliveryMode.PERSISTENT);
        Object retryAttempt = sent.getValue().getMessageProperties().getHeader(
                CustomApiMqConstant.PARSE_RESULT_RETRY_HEADER);
        assertThat(retryAttempt).isEqualTo(2);
    }

    @Test
    void nackConfirmDoesNotAckOriginalDelivery() {
        RabbitTemplate rabbit = mock(RabbitTemplate.class);
        RabbitCustomResultRetryPublisher publisher =
                new RabbitCustomResultRetryPublisher(rabbit, 1000L);
        doAnswer(invocation -> {
            CorrelationData correlation = invocation.getArgument(3);
            correlation.getFuture().complete(new CorrelationData.Confirm(false, "nack"));
            return null;
        }).when(rabbit).send(any(String.class), any(String.class),
                any(Message.class), any(CorrelationData.class));

        assertThatThrownBy(() -> publisher.publish(
                new Message(new byte[0], new MessageProperties()), 1))
                .isInstanceOf(JeecgBootException.class)
                .hasMessageContaining("nack");
    }
}
