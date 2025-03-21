package com.oingmaryho.business.productservice.config.rabbitMQ;

import org.springframework.amqp.core.Queue;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

	@Bean
	public Queue queueErrProduct() {
		return new Queue("queueErrProduct", true); // durable = true
	}

	@Bean
	public Queue queueProduct() {
		return new Queue("queueProduct", true);
	}
}
