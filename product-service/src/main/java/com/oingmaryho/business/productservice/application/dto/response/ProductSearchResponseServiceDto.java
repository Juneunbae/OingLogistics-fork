package com.oingmaryho.business.productservice.application.dto.response;

import java.util.UUID;

public record ProductSearchResponseServiceDto(
	UUID id,
	String productCode,
	String name,
	UUID manageHubId,
	UUID companyId,
	Long price,
	Long stock

) {
}
