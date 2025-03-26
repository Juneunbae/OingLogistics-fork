package com.oingmaryho.business.companyservice.presentation.dto.request;

import java.util.UUID;

import com.oingmaryho.business.companyservice.domain.CompanyType;

public record CompanySearchRequestDto(
	UUID id,
	CompanyType type,
	String name,
	Long managerId,
	UUID manageHubId,
	String address,
	Boolean isDeleted
) {
}
