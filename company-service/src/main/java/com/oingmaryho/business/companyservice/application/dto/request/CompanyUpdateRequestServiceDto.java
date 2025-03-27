package com.oingmaryho.business.companyservice.application.dto.request;

import java.util.UUID;

import com.oingmaryho.business.companyservice.domain.CompanyType;

public record CompanyUpdateRequestServiceDto(
	UUID id,
	String name,
	CompanyType type,
	Long managerId,
	UUID manageHubId,
	String address
) {
}
