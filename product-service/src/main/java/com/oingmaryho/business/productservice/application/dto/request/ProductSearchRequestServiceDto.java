package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record ProductSearchRequestServiceDto(
	UUID id,
	String productCode,
	String name,
	UUID manageHubId,
	UUID companyId,
	String companyName,
	Integer minPrice,
	Integer maxPrice,
	Integer minStock,
	Integer maxStock
) {
}
