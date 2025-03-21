package com.oingmaryho.business.productservice.presentation.dto.request;

public record ProductUpdateRequestDto(
	String companyName,
	String name,
	Integer price,
	Integer stock

) {
}
