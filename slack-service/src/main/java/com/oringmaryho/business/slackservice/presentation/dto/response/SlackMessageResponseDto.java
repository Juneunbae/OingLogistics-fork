package com.oringmaryho.business.slackservice.presentation.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public record SlackMessageResponseDto(UUID id, Long receiverId, String message, LocalDateTime sentAt) {

}
