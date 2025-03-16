package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.List;


public record CompanySearchResponseDto(
	int page,
	int size,
	int sortDirection,
	List<CompanyDetailsSearchResponseDto> companies
) {
}
