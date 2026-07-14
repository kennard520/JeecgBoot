package org.jeecg.modules.custom.api.mq;

import com.rabbitmq.client.Channel;
import org.jeecg.modules.custom.api.service.ICustomApiTaskService;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class CustomApiResultMqConsumerTest {

    @Test
    void successfulOrIdempotentlyIgnoredResultIsExplicitlyAcked() throws Exception {
        Fixture fixture = fixture();

        fixture.consumer.onMessage(message(0), fixture.channel);

        verify(fixture.taskService).handleParseResult(any());
        verify(fixture.channel).basicAck(42L, false);
        verify(fixture.channel, never()).basicNack(any(Long.class), eq(false), any(Boolean.class));
    }

    @Test
    void threeShortFailuresAreConfirmedIntoFirstDelayQueueThenOriginalIsAcked() throws Exception {
        Fixture fixture = fixture();
        doThrow(new IllegalStateException("database unavailable"))
                .when(fixture.taskService).handleParseResult(any());
        Message message = message(0);

        fixture.consumer.onMessage(message, fixture.channel);

        verify(fixture.taskService, times(3)).handleParseResult(any());
        verify(fixture.retryPublisher).publish(message, 1);
        verify(fixture.channel).basicAck(42L, false);
    }

    @Test
    void failedDelayPublishNacksForBrokerRedelivery() throws Exception {
        Fixture fixture = fixture();
        doThrow(new IllegalStateException("database unavailable"))
                .when(fixture.taskService).handleParseResult(any());
        doThrow(new IllegalStateException("retry publish failed"))
                .when(fixture.retryPublisher).publish(any(), eq(1));

        fixture.consumer.onMessage(message(0), fixture.channel);

        verify(fixture.channel).basicNack(42L, false, true);
        verify(fixture.channel, never()).basicAck(any(Long.class), any(Boolean.class));
    }

    @Test
    void poisonMessageAfterThirdDelayIsRejectedToPolicyDlqWithoutRequeue() throws Exception {
        Fixture fixture = fixture();
        doThrow(new IllegalStateException("poison"))
                .when(fixture.taskService).handleParseResult(any());

        fixture.consumer.onMessage(message(3), fixture.channel);

        verify(fixture.retryPublisher, never()).publish(any(), any(Integer.class));
        verify(fixture.channel).basicNack(42L, false, false);
    }

    @Test
    void malformedJsonUsesSameBoundedDelayPath() throws Exception {
        Fixture fixture = fixture();
        MessageProperties properties = properties(0);
        Message malformed = new Message("not-json".getBytes(StandardCharsets.UTF_8), properties);

        fixture.consumer.onMessage(malformed, fixture.channel);

        verify(fixture.retryPublisher).publish(malformed, 1);
        verify(fixture.channel).basicAck(42L, false);

        Method listenerMethod = CustomApiResultMqConsumer.class
                .getMethod("onMessage", Message.class, Channel.class);
        RabbitListener listener = listenerMethod.getAnnotation(RabbitListener.class);
        assertThat(listener).isNotNull();
        assertThat(listener.containerFactory()).isEqualTo("customApiResultListenerContainerFactory");
    }

    @Test
    void productionResultQueueKeepsOriginalArgumentFreeDeclaration() {
        CustomApiRabbitConfig config = new CustomApiRabbitConfig();

        Queue resultQueue = config.customApiParseResultQueue();
        Queue retry60 = config.customApiParseResultRetry60Queue();
        Queue retry300 = config.customApiParseResultRetry300Queue();
        Queue retry900 = config.customApiParseResultRetry900Queue();

        assertThat(resultQueue.getArguments()).isEmpty();
        assertThat(retry60.getArguments()).containsEntry("x-message-ttl", 60_000L);
        assertThat(retry300.getArguments()).containsEntry("x-message-ttl", 300_000L);
        assertThat(retry900.getArguments()).containsEntry("x-message-ttl", 900_000L);
        assertThat(retry900.getArguments())
                .containsEntry("x-dead-letter-exchange", CustomApiMqConstant.PARSE_RESULT_EXCHANGE)
                .containsEntry("x-dead-letter-routing-key", CustomApiMqConstant.PARSE_RESULT_ROUTING_KEY);
    }

    @Test
    void customFactoryExplicitlyUsesManualAckWithoutContainerRetryAdvice() {
        CustomApiRabbitConfig config = new CustomApiRabbitConfig();
        var factory = config.customApiResultListenerContainerFactory(
                mock(SimpleRabbitListenerContainerFactoryConfigurer.class),
                mock(ConnectionFactory.class));

        assertThat(ReflectionTestUtils.getField(factory, "acknowledgeMode"))
                .isEqualTo(AcknowledgeMode.MANUAL);
        assertThat(ReflectionTestUtils.getField(factory, "adviceChain")).isNull();
    }

    private Fixture fixture() {
        ICustomApiTaskService taskService = mock(ICustomApiTaskService.class);
        CustomResultRetryPublisher retryPublisher = mock(CustomResultRetryPublisher.class);
        Channel channel = mock(Channel.class);
        CustomApiResultMqConsumer consumer = new CustomApiResultMqConsumer(
                taskService, retryPublisher, 3, 0L);
        return new Fixture(taskService, retryPublisher, channel, consumer);
    }

    private Message message(int retryAttempt) {
        return new Message("{\"taskId\":\"task-1\"}".getBytes(StandardCharsets.UTF_8),
                properties(retryAttempt));
    }

    private MessageProperties properties(int retryAttempt) {
        MessageProperties properties = new MessageProperties();
        properties.setDeliveryTag(42L);
        properties.setHeader(CustomApiMqConstant.PARSE_RESULT_RETRY_HEADER, retryAttempt);
        return properties;
    }

    private record Fixture(ICustomApiTaskService taskService,
                           CustomResultRetryPublisher retryPublisher,
                           Channel channel,
                           CustomApiResultMqConsumer consumer) {
    }
}
