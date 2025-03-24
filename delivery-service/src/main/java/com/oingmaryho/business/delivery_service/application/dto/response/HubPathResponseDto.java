package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.UUID;

public record HubPathResponseDto(
        UUID departureHubId,
        UUID arriveHubId,
        Integer hubToHubTime,
        Double distance){
}
