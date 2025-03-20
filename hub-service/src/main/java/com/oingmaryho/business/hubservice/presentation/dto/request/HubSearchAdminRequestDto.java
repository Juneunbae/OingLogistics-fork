package com.oingmaryho.business.hubservice.presentation.dto.request;

import java.util.UUID;

public record HubSearchAdminRequestDto(
	UUID id,
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId,
	Boolean isDeleted
) {
}
