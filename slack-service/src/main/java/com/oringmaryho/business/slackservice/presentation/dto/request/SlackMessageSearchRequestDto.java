package com.oringmaryho.business.slackservice.presentation.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record SlackMessageSearchRequestDto(UUID id, Long receiverId, String message, LocalDateTime sentAt, Boolean isDeleted) {
}
