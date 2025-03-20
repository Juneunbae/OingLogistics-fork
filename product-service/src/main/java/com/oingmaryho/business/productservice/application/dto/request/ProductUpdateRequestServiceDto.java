package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record ProductUpdateRequestServiceDto(
	UUID id,
	String name,
	Long price,
	Long stock
) {
}
