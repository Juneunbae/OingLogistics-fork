package com.oringmaryho.business.userservice.config.messaging;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class RabbitMQConfig {

	// Exchanges
	@Value("${rabbitmq.exchanges.user}")
	private String USER_EXCHANGE;

	// Queues
	@Value("${rabbitmq.queues.slack-user-queue}")
	private String SLACK_USER_QUEUE;

	// Routing Keys
	@Value("${rabbitmq.routing-keys.user}")
	private String USER_ROUTING_KEY;

	// 슬랙 서비스에서 사용할 큐 생성
	@Bean
	public Queue slackUserQueue() {
		return new Queue(SLACK_USER_QUEUE, true);
	}

	// 배송 서비스 Exchange
	@Bean
	public TopicExchange userExchange() {
		return new TopicExchange(USER_EXCHANGE);
	}

	@Description(
		"사용자 서비스와 바인딩 (사용자 슬랙 id 인증 메시지 요청 메시지를 받음)"
	)
	@Bean
	public Binding bindSlackToUser(Queue slackUserQueue, TopicExchange userExchange) {
		return BindingBuilder.bind(slackUserQueue).to(userExchange).with(USER_ROUTING_KEY);
	}

	@Bean
	public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.activateDefaultTyping(LaissezFaireSubTypeValidator.instance, ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.PROPERTY); // __TypeId__ 설정
		return new Jackson2JsonMessageConverter(objectMapper);
	}

	@Bean
	public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
		RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
		rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
		return rabbitTemplate;
	}

}