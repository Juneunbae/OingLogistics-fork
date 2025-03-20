package com.oingmaryho.business.hubservice.application.dto.request;

import java.util.UUID;

public record HubRouteUpdateRequestServiceDto(
	UUID departureHubId,
	UUID arriveHubId,
	Integer hubToHubTime,
	Double distance
) {
}
