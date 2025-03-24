package com.oringmaryho.business.slackservice.infrastructure;

import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDto;
import com.oringmaryho.business.slackservice.application.messaging.MessageHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SlackMessageListener {

  private final MessageHandler messageHandler;

  @RabbitListener(queues = "${rabbitmq.queues.slack-user-queue}")
  public void receiveUserMessage(SlackMessageDto dto) {
    try {
      log.info("Received message: id={}, message={}", dto.id(), dto.message());
      messageHandler.handleMessage(dto);
    } catch (Exception e) {
      log.error("Failed to process message: {}", dto, e);
      throw new RuntimeException("Failed to process message", e);
    }
  }

  //주문
  @RabbitListener(queues = "${rabbitmq.queues.slack-order-queue}")
  public void receiveOrderMessage(SlackMessageDto dto) {
    log.info("Received from order queue: id={}, message={}", dto.id(), dto.message());
    messageHandler.handleMessage(dto);
  }

}