package com.oingmaryho.business.hubservice.presentation.dto.request;

import java.util.UUID;

public record HubRouteSearchAdminRequestDto(
	UUID id,
	UUID departureHubId,
	UUID arriveHubId,
	Boolean isDeleted
) {
}
