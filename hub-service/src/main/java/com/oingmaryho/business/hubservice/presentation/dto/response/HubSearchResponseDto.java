package com.oingmaryho.business.hubservice.presentation.dto.response;

import java.util.UUID;

public record HubSearchResponseDto(
	UUID id,
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId
) {
}
