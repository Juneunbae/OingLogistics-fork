package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record ProductCreateRequestServiceDto(
	String name,
	UUID manageHubId,
	Long stock,
	Long price
) {
}
