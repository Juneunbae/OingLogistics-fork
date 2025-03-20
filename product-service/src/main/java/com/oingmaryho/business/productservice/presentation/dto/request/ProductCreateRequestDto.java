package com.oingmaryho.business.productservice.presentation.dto.request;

import java.util.UUID;

public record ProductCreateRequestDto(
	UUID companyId,
	String productCode,
	String name,
	UUID manageHubId,
	Long stock,
	Long price
) {
}
