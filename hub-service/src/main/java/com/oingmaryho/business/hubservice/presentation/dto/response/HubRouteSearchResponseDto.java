package com.oingmaryho.business.hubservice.presentation.dto.response;

import java.util.UUID;

public record HubRouteSearchResponseDto(
	UUID departureHubId,
	UUID arriveHubId,
	Integer hubToHubTime,
	Double distance
) {
}
