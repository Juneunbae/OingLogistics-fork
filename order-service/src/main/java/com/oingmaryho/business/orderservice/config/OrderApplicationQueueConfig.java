package com.oingmaryho.business.orderservice.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
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
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY); // __TypeId__ 설정
        return new Jackson2JsonMessageConverter(objectMapper);
    }

    @Value("${message.product.exchange}")
    private String productExchange;

    @Value("${message.queue.product}")
    private String queueProduct;

    @Value("${message.product.err.exchange}")
    private String productErrExchange;

    @Value("${message.queue.err.product}")
    private String queueErrProduct;

    @Value("${message.delivery.exchange}")
    private String deliveryExchange;

    @Value("${message.queue.delivery}")
    private String queueDelivery;

    @Value("${message.slack.exchange}")
    private String slackExchange;

    @Value("${message.queue.slack}")
    private String queueSlack;

    @Value("${message.queue.order}")
    private String queueOrder;

    @Value("${message.queue.user-order-queue}")
    private String queueUserOrderQueue;

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(productExchange);
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
        return new TopicExchange(productErrExchange);
    }

    @Bean
    public Queue queueErrProduct() {
        return new Queue(queueErrProduct);
    }

    @Bean
    public Binding bindingErrProduct() {
        return BindingBuilder.bind(queueErrProduct()).to(errExchange()).with(productErrExchange);
    }

    @Bean
    public TopicExchange deliveryExchange() {
        return new TopicExchange(deliveryExchange);
    }

    @Bean
    public Queue queueDelivery() {
        return new Queue(queueDelivery);
    }

    @Bean
    public Binding bindingDelivery() {
        return BindingBuilder.bind(queueDelivery()).to(deliveryExchange()).with(deliveryExchange);
    }

    @Bean
    public TopicExchange slackExchange() {
        return new TopicExchange(slackExchange);
    }

    @Bean
    public Queue queueSlack() {
        return new Queue(queueSlack);
    }

    @Bean
    public Binding bindingSlack() {
        return BindingBuilder.bind(queueSlack()).to(slackExchange()).with(slackExchange);
    }

    @Bean
    public Queue queueOrder() {
        return new Queue(queueOrder);
    }

    @Bean
    public Queue queueUserOrderQueue() {
        return new Queue(queueUserOrderQueue);
    }
}