package com.oingmaryho.business.companyservice.presentation.dto.request;

import java.util.UUID;

import com.oingmaryho.business.companyservice.domain.CompanyType;

public record CompanyAdminCreateRequestDto(
	String name,
	CompanyType type,
	Long managerId,
	UUID manageHubId,
	String address

) {
}
