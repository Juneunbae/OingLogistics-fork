package com.oingmaryho.business.hubservice.application.dto.response;

import java.util.UUID;

public record HubRouteSearchResponseServiceDto(
	UUID departureHubId,
	UUID arriveHubId,
	Integer hubToHubTime,
	Double distance
) {
}
