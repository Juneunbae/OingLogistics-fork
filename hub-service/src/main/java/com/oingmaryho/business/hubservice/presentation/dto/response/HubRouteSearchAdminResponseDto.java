package com.oingmaryho.business.hubservice.presentation.dto.response;

import java.util.UUID;

public record HubRouteSearchAdminResponseDto(
	UUID id,
	UUID departureHubId,
	UUID arriveHubId,
	Integer hubToHubTime,
	Double distance,
	Boolean isDeleted
) {
}
