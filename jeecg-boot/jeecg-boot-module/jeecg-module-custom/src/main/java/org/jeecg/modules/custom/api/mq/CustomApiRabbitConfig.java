package org.jeecg.modules.custom.api.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.boot.autoconfigure.amqp.RabbitTemplateCustomizer;
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
        return QueueBuilder.durable(CustomApiMqConstant.PARSE_RESULT_QUEUE).build();
    }

    @Bean
    public Binding customApiParseResultBinding(Queue customApiParseResultQueue,
                                               DirectExchange customApiParseResultExchange) {
        return BindingBuilder.bind(customApiParseResultQueue)
                .to(customApiParseResultExchange)
                .with(CustomApiMqConstant.PARSE_RESULT_ROUTING_KEY);
    }
}
