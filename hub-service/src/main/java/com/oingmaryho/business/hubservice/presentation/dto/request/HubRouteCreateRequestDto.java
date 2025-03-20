package com.oingmaryho.business.hubservice.presentation.dto.request;

import java.util.UUID;

public record HubRouteCreateRequestDto(
	UUID departureHubId,
	UUID arriveHubId,
	Integer hubToHubTime,
	Double distance
) {
}
