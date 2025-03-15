package com.oingmaryho.business.hubservice.presentation.dto.request;

public record HubSearchRequestDto(
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId
) {
}
