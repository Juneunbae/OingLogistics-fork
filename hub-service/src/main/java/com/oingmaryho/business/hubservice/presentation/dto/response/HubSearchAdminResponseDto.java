package com.oingmaryho.business.hubservice.presentation.dto.response;

import java.util.UUID;

public record HubSearchAdminResponseDto(
	UUID id,
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId,
	Boolean isDeleted
) {
}
