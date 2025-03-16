package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.List;


public record CompanySearchResponseServiceDto(
	Integer page,
	Integer size,
	Integer sortDirection,
	List<CompanyDetailsSearchResponseServiceDto> companies
) {
}
