package com.oingmaryho.business.hubservice.application.dto.request;

public record HubCreateRequestServiceDto(
	String name,
	String address,
	Long managerId
) {
}
