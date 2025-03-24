package com.oringmaryho.business.userservice.application.messaging;

import com.oringmaryho.business.userservice.application.dto.request.SlackMessageDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserMessageService {

  private final MessagePublisher messagePublisher;
  private final RabbitTemplate rabbitTemplate;

  @Value("${rabbitmq.exchanges.user}")
  private String USER_EXCHANGE;
  @Value("${rabbitmq.routing-keys.user}")
  private String USER_ROUTING_KEY;


  public void sendSlackMessage(Long id, String message) {
    SlackMessageDto dto = new SlackMessageDto(id, message);
    messagePublisher.publishSlackMessage(dto);
  }
}
