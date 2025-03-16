package com.oingmaryho.business.hubservice.presentation.dto.response;

public record HubSearchResponseDto(
	Long hubId,
	String name,
	String address,
	Double latitude,
	Double longitude,
	Long managerId
) {
}
