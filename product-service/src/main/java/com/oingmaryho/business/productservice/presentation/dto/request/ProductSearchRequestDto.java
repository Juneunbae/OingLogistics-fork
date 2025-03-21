package com.oingmaryho.business.productservice.presentation.dto.request;

import java.util.UUID;

public record ProductSearchRequestDto(
	UUID id,
	String productCode,
	String name,
	UUID manageHubId,
	UUID companyId,
	String companyName,
	Long minPrice,
	Long maxPrice,
	Long minStock,
	Long maxStock
) {
}
