package com.oringmaryho.business.userservice.infrastructure;

import com.oringmaryho.business.userservice.application.dto.request.SlackMessageDto;
import com.oringmaryho.business.userservice.application.messaging.MessagePublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RabbitMQMessagePublisher implements MessagePublisher {

  private final RabbitTemplate rabbitTemplate;
  @Value("${rabbitmq.exchanges.user}")
  private String USER_EXCHANGE;
  @Value("${rabbitmq.routing-keys.user}")
  private String USER_ROUTING_KEY;

  @Value("${rabbitmq.routing-keys.hub}")
  private String HUB_ROUTING_KEY;

  @Value("${rabbitmq.routing-keys.delivery}")
  private String DELIVERY_ROUTING_KEY;

  @Value("${rabbitmq.routing-keys.product}")
  private String PRODUCT_ROUTING_KEY;

  @Value("${rabbitmq.routing-keys.order}")
  private String ORDER_ROUTING_KEY;

  @Override
  public void publishSlackMessage(SlackMessageDto dto) {
    rabbitTemplate.convertAndSend(USER_EXCHANGE, USER_ROUTING_KEY, dto);
    log.info(
        "Message published successfully USER_EXCHANGE: ${}, USER_ROUTING_KEY: ${}, id: ${}, msg: ${}",
        USER_EXCHANGE, USER_ROUTING_KEY, dto.id(), dto.message());
  }

  @Override
  public void publishUserStatus(Long id) {
    rabbitTemplate.convertAndSend(USER_EXCHANGE, HUB_ROUTING_KEY, id);
    rabbitTemplate.convertAndSend(USER_EXCHANGE, DELIVERY_ROUTING_KEY, id);
    rabbitTemplate.convertAndSend(USER_EXCHANGE, PRODUCT_ROUTING_KEY, id);
    rabbitTemplate.convertAndSend(USER_EXCHANGE, ORDER_ROUTING_KEY, String.valueOf(id));
    log.info("Message published successfully id: ${}", id);
  }
}
