package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record ProductUpdateRequestServiceDto(
	UUID id,
	String companyName,
	String name,
	Integer price,
	Integer stock
) {
}
