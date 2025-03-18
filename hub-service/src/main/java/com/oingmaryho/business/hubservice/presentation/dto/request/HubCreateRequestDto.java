package com.oingmaryho.business.hubservice.presentation.dto.request;

public record HubCreateRequestDto(
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId
) {
}
