package com.oingmaryho.business.hubservice.application.dto.response;

import java.util.UUID;

public record HubSearchResponseServiceDto(
	UUID id,
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId
) {
}
