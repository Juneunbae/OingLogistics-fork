package com.oringmaryho.business.userservice.application.messaging;

import com.oringmaryho.business.userservice.application.dto.request.SlackMessageDto;

public interface MessagePublisher {
  void publishSlackMessage(SlackMessageDto dto);

  void publishUserStatus(Long id);
}
