package com.oingmaryho.business.productservice.presentation.dto.request;

import java.util.UUID;

public record ProductUpdateRequestDto(
	String name,
	UUID manageHubId,
	Integer price,
	Integer stock

) {
}
