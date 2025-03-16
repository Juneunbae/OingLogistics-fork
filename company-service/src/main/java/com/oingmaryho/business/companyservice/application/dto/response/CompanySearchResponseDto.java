package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.List;


public record CompanySearchResponseDto(
	Integer page,
	Integer size,
	Integer sortDirection,
	List<CompanyDetailsSearchResponseDto> companies
) {
}
