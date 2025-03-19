package com.oingmaryho.business.hubservice.application.dto.request;

public record HubUpdateRequestServiceDto(
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId
) {
}
