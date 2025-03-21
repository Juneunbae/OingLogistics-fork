package com.oingmaryho.business.productservice.presentation.dto.response;

import java.util.UUID;

public record ProductDetailsSearchResponseDto(
	UUID id,
	String name,
	UUID companyId,
	String companyName,
	UUID manageHubId,
	Integer price,
	Integer stock
) {
}
