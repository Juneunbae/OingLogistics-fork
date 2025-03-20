package com.oingmaryho.business.productservice.presentation.dto.request;

public record ProductUpdateRequestDto(
	String name,
	Long price,
	Long stock

) {
}
