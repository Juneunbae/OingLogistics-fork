package com.oingmaryho.business.productservice.presentation.dto.request;

public record ProductUpdateRequestDto(
	String companyName,
	String name,
	Long price,
	Long stock

) {
}
