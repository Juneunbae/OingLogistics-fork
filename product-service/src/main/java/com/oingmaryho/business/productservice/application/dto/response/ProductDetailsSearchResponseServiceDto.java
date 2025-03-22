package com.oingmaryho.business.productservice.application.dto.response;

import java.util.UUID;

public record ProductDetailsSearchResponseServiceDto(
	UUID id,
	String name,
	UUID companyId,
	String companyName,
	UUID manageHubId,
	Integer price,
	Integer stock

) {
}
