package com.oingmaryho.business.hubservice.application.dto.response;

import java.util.UUID;

public record HubRouteSearchAdminResponseServiceDto(
	UUID id,
	UUID departureHubId,
	UUID arriveHubId,
	Integer hubToHubTime,
	Double distance,
	Boolean isDeleted
) {
}
