package com.oingmaryho.business.hubservice.application.dto.request;

import java.util.UUID;

public record HubsSearchRequestServiceDto(
	UUID id,
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId
) {
}
