package com.oingmaryho.business.productservice.presentation.dto.response;

import java.util.UUID;

import com.oingmaryho.business.productservice.presentation.dto.request.ProductSearchRequestDto;

public record ProductSearchResponseDto(
	UUID id,
	String productCode,
	String name,
	UUID manageHubId,
	UUID companyId,
	Long price,
	Long stock
) {
}
