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

  @RabbitListener(queues = "${message.queue.slack-user-queue}")
  public void receiveUserMessage(SlackMessageDto dto) {
    try {
      log.info("Received message: id={}, message={}", dto.id(), dto.message());
      messageHandler.handleMessage(dto);
    } catch (Exception e) {
      log.error("Failed to process message: {}", dto, e);
      throw new RuntimeException("Failed to process message", e);
    }
  }

  //그 외 서비스들
  @RabbitListener(queues = "${message.queue.slack-others-queue}")
  public void receiveOtherMessage(SlackMessageDto dto) {
    try {
      log.info("Received message: id={}, message={}", dto.id(), dto.message());
      messageHandler.handleMessage(dto);
    } catch (Exception e) {
      log.error("Failed to process message: {}", dto, e);
      throw new RuntimeException("Failed to process message", e);
    }
  }

}