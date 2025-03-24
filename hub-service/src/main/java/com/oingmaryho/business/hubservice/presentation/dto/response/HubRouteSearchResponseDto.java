package com.oingmaryho.business.hubservice.presentation.dto.response;

import java.util.UUID;

public record HubRouteSearchResponseDto(
	UUID id,
	UUID departureHubId,
	String departureHubName,
	UUID arriveHubId,
	String arriveHubName,
	Integer hubToHubTime,
	Double distance
) {
}
