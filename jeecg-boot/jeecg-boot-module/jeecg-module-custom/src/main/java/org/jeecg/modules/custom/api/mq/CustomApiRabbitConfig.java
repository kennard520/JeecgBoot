package org.jeecg.modules.custom.api.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CustomApiRabbitConfig {

    @Bean
    public RabbitTemplateCustomizer customApiRabbitTemplateCustomizer() {
        return template -> template.setMandatory(true);
    }

    @Bean
    public DirectExchange customApiParseRequestExchange() {
        return new DirectExchange(CustomApiMqConstant.PARSE_REQUEST_EXCHANGE, true, false);
    }

    @Bean
    public DirectExchange customApiParseResultExchange() {
        return new DirectExchange(CustomApiMqConstant.PARSE_RESULT_EXCHANGE, true, false);
    }

    @Bean
    public Queue customApiParseResultQueue() {
        // The production queue predates DLX arguments. Rabbit policy owns its DLX to avoid 406 redeclaration.
        return QueueBuilder.durable(CustomApiMqConstant.PARSE_RESULT_QUEUE).build();
    }

    @Bean
    public Binding customApiParseResultBinding(Queue customApiParseResultQueue,
                                               DirectExchange customApiParseResultExchange) {
        return BindingBuilder.bind(customApiParseResultQueue)
                .to(customApiParseResultExchange)
                .with(CustomApiMqConstant.PARSE_RESULT_ROUTING_KEY);
    }

    @Bean
    public DirectExchange customApiParseResultDeadLetterExchange() {
        return new DirectExchange(CustomApiMqConstant.PARSE_RESULT_DLX, true, false);
    }

    @Bean
    public Queue customApiParseResultDeadLetterQueue() {
        return QueueBuilder.durable(CustomApiMqConstant.PARSE_RESULT_DLQ).build();
    }

    @Bean
    public Binding customApiParseResultDeadLetterBinding(
            Queue customApiParseResultDeadLetterQueue,
            DirectExchange customApiParseResultDeadLetterExchange) {
        return BindingBuilder.bind(customApiParseResultDeadLetterQueue)
                .to(customApiParseResultDeadLetterExchange)
                .with(CustomApiMqConstant.PARSE_RESULT_DLQ_ROUTING_KEY);
    }

    @Bean
    public DirectExchange customApiParseResultRetryExchange() {
        return new DirectExchange(CustomApiMqConstant.PARSE_RESULT_RETRY_EXCHANGE, true, false);
    }

    @Bean
    public Queue customApiParseResultRetry60Queue() {
        return retryQueue(CustomApiMqConstant.PARSE_RESULT_RETRY_60_QUEUE, 60_000L);
    }

    @Bean
    public Queue customApiParseResultRetry300Queue() {
        return retryQueue(CustomApiMqConstant.PARSE_RESULT_RETRY_300_QUEUE, 300_000L);
    }

    @Bean
    public Queue customApiParseResultRetry900Queue() {
        return retryQueue(CustomApiMqConstant.PARSE_RESULT_RETRY_900_QUEUE, 900_000L);
    }

    @Bean
    public Binding customApiParseResultRetry60Binding(
            @Qualifier("customApiParseResultRetry60Queue") Queue queue,
            @Qualifier("customApiParseResultRetryExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(CustomApiMqConstant.PARSE_RESULT_RETRY_60_ROUTING_KEY);
    }

    @Bean
    public Binding customApiParseResultRetry300Binding(
            @Qualifier("customApiParseResultRetry300Queue") Queue queue,
            @Qualifier("customApiParseResultRetryExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(CustomApiMqConstant.PARSE_RESULT_RETRY_300_ROUTING_KEY);
    }

    @Bean
    public Binding customApiParseResultRetry900Binding(
            @Qualifier("customApiParseResultRetry900Queue") Queue queue,
            @Qualifier("customApiParseResultRetryExchange") DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange)
                .with(CustomApiMqConstant.PARSE_RESULT_RETRY_900_ROUTING_KEY);
    }

    @Bean(name = "customApiResultListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory customApiResultListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    private Queue retryQueue(String name, long ttlMillis) {
        return QueueBuilder.durable(name)
                .withArgument("x-message-ttl", ttlMillis)
                .deadLetterExchange(CustomApiMqConstant.PARSE_RESULT_EXCHANGE)
                .deadLetterRoutingKey(CustomApiMqConstant.PARSE_RESULT_ROUTING_KEY)
                .build();
    }
}
