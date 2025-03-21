package com.oingmaryho.business.hubservice.presentation.dto.request;

public record HubCreateRequestDto(
	String name,
	String address,
	Long managerId
) {
}
