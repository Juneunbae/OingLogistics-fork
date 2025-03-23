package com.oingmaryho.business.hubservice.application.dto.request;

import java.util.UUID;

public record HubRoutesSearchRequestServiceDto(
	UUID id,
	UUID departureHubId,
	UUID arriveHubId
) {
}
