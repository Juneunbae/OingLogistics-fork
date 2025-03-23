package com.oringmaryho.business.slackservice.application.dto.request;

import java.time.LocalDateTime;
import java.util.UUID;

public record SlackMessageSearchRequestServiceDto(UUID id, Long receiverId, String message, LocalDateTime sentAt,Boolean isDeleted) {

}
