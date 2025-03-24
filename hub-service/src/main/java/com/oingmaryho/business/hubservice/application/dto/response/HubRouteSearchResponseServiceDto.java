package com.oingmaryho.business.hubservice.application.dto.response;

import java.util.UUID;

public record HubRouteSearchResponseServiceDto(
	UUID id,
	UUID departureHubId,
	String departureHubName,
	UUID arriveHubId,
	String arriveHubName,
	Integer hubToHubTime,
	Double distance
) {
}
