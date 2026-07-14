package org.jeecg.modules.custom.api.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.config.RetryInterceptorBuilder;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.retry.RejectAndDontRequeueRecoverer;
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
        return QueueBuilder.durable(CustomApiMqConstant.PARSE_RESULT_QUEUE)
                .deadLetterExchange(CustomApiMqConstant.PARSE_RESULT_DLX)
                .deadLetterRoutingKey(CustomApiMqConstant.PARSE_RESULT_DLQ_ROUTING_KEY)
                .build();
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

    @Bean(name = "customApiResultListenerContainerFactory")
    public SimpleRabbitListenerContainerFactory customApiResultListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setDefaultRequeueRejected(false);
        factory.setAdviceChain(RetryInterceptorBuilder.stateless()
                .maxAttempts(3)
                .backOffOptions(1000L, 2.0, 5000L)
                .recoverer(new RejectAndDontRequeueRecoverer())
                .build());
        return factory;
    }
}
