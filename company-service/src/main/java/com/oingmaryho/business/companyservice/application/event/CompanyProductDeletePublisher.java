package com.oingmaryho.business.companyservice.application.event;

import com.oingmaryho.business.companyservice.application.dto.request.CompanyProductDeleteRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CompanyProductDeletePublisher {

	private final RabbitTemplate rabbitTemplate;

	@Value("${rabbitmq.exchange.company}")
	private String companyExchange;

	@Value("${rabbitmq.routing-key.company-deleted}")
	private String companyDeletedRoutingKey;

	public void publish(CompanyProductDeleteRequestDto message) {
		rabbitTemplate.convertAndSend(companyExchange, companyDeletedRoutingKey, message);
	}
}
