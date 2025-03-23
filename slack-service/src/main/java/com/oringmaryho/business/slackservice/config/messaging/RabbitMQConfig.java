package com.oringmaryho.business.slackservice.config.messaging;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

@Configuration
public class RabbitMQConfig {

	// Exchanges
	@Value("${rabbitmq.exchanges.user}")
	private String USER_EXCHANGE;

	@Value("${rabbitmq.exchanges.delivery}")
	private String DELIVERY_EXCHANGE;

	@Value("${rabbitmq.exchanges.order}")
	private String ORDER_EXCHANGE;

	@Value("${rabbitmq.exchanges.product}")
	private String PRODUCT_EXCHANGE;

	// Queues
	@Value("${rabbitmq.queues.slack-user-queue}")
	private String SLACK_USER_QUEUE;

	@Value("${rabbitmq.queues.slack-delivery-queue}")
	private String SLACK_DELIVERY_QUEUE;

	@Value("${rabbitmq.queues.slack-order-queue}")
	private String SLACK_ORDER_QUEUE;

	@Value("${rabbitmq.queues.slack-product-queue}")
	private String SLACK_PRODUCT_QUEUE;

	// Routing Keys
	@Value("${rabbitmq.routing-keys.user}")
	private String USER_ROUTING_KEY;

	@Value("${rabbitmq.routing-keys.delivery}")
	private String DELIVERY_ROUTING_KEY;

	@Value("${rabbitmq.routing-keys.order}")
	private String ORDER_ROUTING_KEY;

	@Value("${rabbitmq.routing-keys.product}")
	private String PRODUCT_ROUTING_KEY;

	// 슬랙 서비스에서 사용할 큐 생성
	@Bean
	public Queue slackUserQueue() {
		return new Queue(SLACK_USER_QUEUE, true);
	}

	@Bean
	public Queue slackDeliveryQueue() {
		return new Queue(SLACK_DELIVERY_QUEUE, true);
	}

	@Bean
	public Queue slackOrderQueue() {
		return new Queue(SLACK_ORDER_QUEUE, true);
	}

	@Bean
	public Queue slackProductQueue() {
		return new Queue(SLACK_PRODUCT_QUEUE, true);
	}

	// 배송 서비스 Exchange
	@Bean
	public TopicExchange userExchange() {
		return new TopicExchange(USER_EXCHANGE);
	}

	@Bean
	public TopicExchange deliveryExchange() {
		return new TopicExchange(DELIVERY_EXCHANGE);
	}

	// 주문 서비스 Exchange
	@Bean
	public TopicExchange orderExchange() {
		return new TopicExchange(ORDER_EXCHANGE);
	}

	// 상품 서비스 Exchange
	@Bean
	public TopicExchange productExchange() {
		return new TopicExchange(PRODUCT_EXCHANGE);
	}

	@Description(
		"사용자 서비스와 바인딩 (사용자 슬랙 id 인증 메시지 요청 메시지를 받음)"
	)
	@Bean
	public Binding bindSlackToUser(Queue slackUserQueue, TopicExchange userExchange) {
		return BindingBuilder.bind(slackUserQueue).to(userExchange).with(USER_ROUTING_KEY);
	}

	@Description(
		"배송 서비스와 바인딩 (배송 관련 메시지를 받음)"
	)
	@Bean
	public Binding bindSlackToDelivery(Queue slackDeliveryQueue, TopicExchange deliveryExchange) {
		return BindingBuilder.bind(slackDeliveryQueue).to(deliveryExchange).with(DELIVERY_ROUTING_KEY);
	}

	@Description(
		"주문 서비스와 바인딩 (주문 관련 메시지를 받음)"
	)
	@Bean
	public Binding bindSlackToOrder(Queue slackOrderQueue, TopicExchange orderExchange) {
		return BindingBuilder.bind(slackOrderQueue).to(orderExchange).with(ORDER_ROUTING_KEY);
	}

	@Description(
		"상품 서비스와 바인딩 (상품 관련 메시지를 받음)"
	)
	@Bean
	public Binding bindSlackToProduct(Queue slackProductQueue, TopicExchange productExchange) {
		return BindingBuilder.bind(slackProductQueue).to(productExchange).with(PRODUCT_ROUTING_KEY);
	}
}