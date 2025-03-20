package com.oingmaryho.business.hubservice.presentation.dto.request;

import java.util.UUID;

public record HubRouteUpdateRequestDto(
	UUID departureHubId,
	UUID arriveHubId,
	Integer hubToHubTime,
	Double distance
) {
}
