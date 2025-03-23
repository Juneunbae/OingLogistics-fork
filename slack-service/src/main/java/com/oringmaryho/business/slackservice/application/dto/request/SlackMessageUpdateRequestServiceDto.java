package com.oringmaryho.business.slackservice.application.dto.request;

import java.util.UUID;

public record SlackMessageUpdateRequestServiceDto(UUID id, String message) {

}
