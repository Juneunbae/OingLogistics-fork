package com.oingmaryho.business.hubservice.application.dto.request;

import java.util.UUID;

public record HubPathRequestServiceDto(
	UUID departureHubId,
	UUID arriveHubId
) {
}
