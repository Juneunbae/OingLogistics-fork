package com.oringmaryho.business.slackservice.presentation.dto.request;

import java.util.UUID;

public record SlackMessageUpdateRequestDto(UUID id, String message) {

}
