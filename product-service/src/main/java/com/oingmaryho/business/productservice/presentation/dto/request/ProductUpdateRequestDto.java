package com.oingmaryho.business.productservice.presentation.dto.request;

import java.util.UUID;

public record ProductUpdateRequestDto(
	String companyName,
	String name,
	UUID manageHubId,
	Integer price,
	Integer stock

) {
}
