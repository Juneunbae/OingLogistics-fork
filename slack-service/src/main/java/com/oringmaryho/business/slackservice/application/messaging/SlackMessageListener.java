package com.oringmaryho.business.slackservice.application.messaging;

import com.oringmaryho.business.slackservice.application.service.SlackAdminMessageService;
import com.oringmaryho.business.slackservice.domain.SlackMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SlackMessageListener {
	private final SlackAdminMessageService slackAdminMessageService;

	@RabbitListener(queues = "${rabbitmq.queues.slack-user-queue}")
	public void receiveUserMessage(SlackMessage message) {
		//todo: 사용자에서 받은 요청 처리

	}

	@RabbitListener(queues = "${rabbitmq.queues.slack-delivery-queue}")
	public void receiveDeliveryMessage(SlackMessage message) {
		//todo: 배송에서 받은 요청 처리

	}

	@RabbitListener(queues = "${rabbitmq.queues.slack-order-queue}")
	public void receiveOrderMessage(SlackMessage message) {
		//todo: 주문에서 받은 요청 처리

	}

	@RabbitListener(queues = "${rabbitmq.queues.slack-product-queue}")
	public void receiveProductMessage(SlackMessage message) {
		//todo: 상품에서 받은 요청 처리

	}
}