package com.oingmaryho.business.hubservice.application.dto.request;

import java.util.UUID;

public record HubRoutesSearchAdminRequestServiceDto(
	UUID id,
	UUID departureHubId,
	UUID arriveHubId,
	Boolean isDeleted
) {
}
