package com.oingmaryho.business.companyservice.presentation.dto.request;

import java.util.UUID;

public record CompanySearchRequestDto(
	UUID id,
	String type,
	String name
) {
}
