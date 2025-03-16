package com.oingmaryho.business.companyservice.presentation.dto.response;

import java.util.List;

public record CompanySearchResponseDto(
	int page,
	int size,
	int sortDirection,
	List<CompanyDetailsSearchResponseDto> companies
) {

}
