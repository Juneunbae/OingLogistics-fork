package com.oingmaryho.business.hubservice.presentation.dto.request;

import java.util.UUID;

public record HubRouteSearchRequestDto (
	UUID id,
	UUID departureHubId,
	UUID arriveHubId
) {
}
