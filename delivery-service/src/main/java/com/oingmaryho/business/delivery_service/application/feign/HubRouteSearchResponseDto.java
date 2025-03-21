package com.oingmaryho.business.delivery_service.application.feign;

import java.util.UUID;

public record HubRouteSearchResponseDto(
        UUID departureHubId,
        UUID arriveHubId,
        Integer hubToHubTime,
        Double distance
) {
}
