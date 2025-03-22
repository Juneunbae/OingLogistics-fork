package com.oingmaryho.business.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderApplicationQueueConfig {
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Value("${message.exchange}")
    private String exchange;

    @Value("${message.queue.product}")
    private String queueProduct;

    @Value("${message.err.exchange}")
    private String errExchange;

    @Value("${message.queue.err.product}")
    private String queueErrProduct;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Queue queueProduct() {
        return new Queue(queueProduct);
    }

    @Bean
    public Binding bindingProduct() {
        return BindingBuilder.bind(queueProduct()).to(exchange()).with(queueProduct);
    }

    @Bean
    public TopicExchange errExchange() {
        return new TopicExchange(errExchange);
    }

    @Bean
    public Queue queueErrProduct() {
        return new Queue(queueErrProduct);
    }

    @Bean
    public Binding bindingErrProduct() {
        return BindingBuilder.bind(queueErrProduct()).to(errExchange()).with(errExchange);
    }
}