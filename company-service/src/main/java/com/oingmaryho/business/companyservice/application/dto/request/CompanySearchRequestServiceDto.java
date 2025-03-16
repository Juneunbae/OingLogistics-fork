package com.oingmaryho.business.companyservice.application.dto.request;

import org.springframework.data.domain.Pageable;

public record CompanySearchRequestServiceDto(
	String type,
	String name,
	Pageable pageable
) {
}
