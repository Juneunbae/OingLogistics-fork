package com.oingmaryho.business.companyservice.application.dto.request;

import java.awt.print.Pageable;

public record CompanySearchRequestServiceDto(
	String type,
	String name,
	Pageable pageable
) {
}
