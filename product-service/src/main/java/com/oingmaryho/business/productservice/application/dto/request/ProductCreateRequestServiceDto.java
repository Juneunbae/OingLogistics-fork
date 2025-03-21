package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record ProductCreateRequestServiceDto(
	UUID companyId,
	String companyName,
	String productCode,
	String name,
	UUID manageHubId,
	Integer stock,
	Integer price
) {
}
