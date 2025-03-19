package com.oingmaryho.business.hubservice.application.dto.request;

import java.util.UUID;

public record HubsSearchAdminRequestServiceDto(
	UUID id,
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId,
	Boolean isDeleted
) {
}
