package com.oringmaryho.business.slackservice.application.dto.request;

import java.util.UUID;

public record SlackMessageDeleteRequestServiceDto(Long userId, UUID id) {

}
