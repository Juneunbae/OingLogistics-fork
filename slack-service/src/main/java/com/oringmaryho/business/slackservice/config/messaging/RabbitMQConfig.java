package com.oringmaryho.business.slackservice.config.messaging;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.impl.LaissezFaireSubTypeValidator;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.exchanges.user}")
  private String USER_EXCHANGE;

  @Value("${rabbitmq.queues.slack-user-queue}")
  private String SLACK_USER_QUEUE;

  @Value("${rabbitmq.queues.user-hub-queue}")
  private String USER_HUB_QUEUE;

  @Value("${rabbitmq.queues.user-delivery-queue}")
  private String USER_DELIVERY_QUEUE;

  @Value("${rabbitmq.queues.user-product-queue}")
  private String USER_PRODUCT_QUEUE;

  @Value("${rabbitmq.routing-keys.user}")
  private String USER_ROUTING_KEY;

  @Value("${rabbitmq.routing-keys.hub}")
  private String HUB_ROUTING_KEY;

  @Value("${rabbitmq.routing-keys.delivery}")
  private String DELIVERY_ROUTING_KEY;

  @Value("${rabbitmq.routing-keys.product}")
  private String PRODUCT_ROUTING_KEY;

  @Bean
  public Queue slackUserQueue() {
    return new Queue(SLACK_USER_QUEUE, true);
  }

  @Bean
  public Queue userHubQueue() {
    return new Queue(USER_HUB_QUEUE, true);
  }

  @Bean
  public Queue userDeliveryQueue() {
    return new Queue(USER_DELIVERY_QUEUE, true);
  }

  @Bean
  public Queue userProductQueue() {
    return new Queue(USER_PRODUCT_QUEUE, true);
  }

  @Bean
  public TopicExchange userExchange() {
    return new TopicExchange(USER_EXCHANGE);
  }

  @Bean
  public Binding bindSlackToUser(Queue slackUserQueue, TopicExchange userExchange) {
    return BindingBuilder.bind(slackUserQueue).to(userExchange).with(USER_ROUTING_KEY);
  }

  @Bean
  public Binding bindHubQueue(Queue userHubQueue, TopicExchange userExchange) {
    return BindingBuilder.bind(userHubQueue).to(userExchange).with(HUB_ROUTING_KEY);
  }

  @Bean
  public Binding bindDeliveryQueue(Queue userDeliveryQueue, TopicExchange userExchange) {
    return BindingBuilder.bind(userDeliveryQueue).to(userExchange).with(DELIVERY_ROUTING_KEY);
  }

  @Bean
  public Binding bindProductQueue(Queue userProductQueue, TopicExchange userExchange) {
    return BindingBuilder.bind(userProductQueue).to(userExchange).with(PRODUCT_ROUTING_KEY);
  }

  @Bean
  public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance,
        ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY); // __TypeId__ 설정
    return new Jackson2JsonMessageConverter(objectMapper);
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    return rabbitTemplate;
  }
}