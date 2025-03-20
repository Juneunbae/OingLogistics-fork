package com.oingmaryho.business.productservice.presentation.dto.request;

import java.util.UUID;

public record ProductCreateRequestDto(
	String name,
	UUID manageHubId,
	Long stock,
	Long price
) {
}
