package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record ProductSearchRequestServiceDto(
	UUID id,
	String productCode,
	String name,
	UUID manageHubId,
	UUID companyId,
	Long minPrice,
	Long maxPrice,
	Long minStock,
	Long maxStock
) {
}
