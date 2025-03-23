package com.oringmaryho.business.slackservice.presentation.dto.request;

import java.util.UUID;

public record SlackMessageRequestDto(UUID id, String message) {

}
