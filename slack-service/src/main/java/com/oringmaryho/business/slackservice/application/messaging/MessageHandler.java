package com.oringmaryho.business.slackservice.application.messaging;

import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDto;

public interface MessageHandler {
  void handleMessage(SlackMessageDto slackMessageDto);
}
