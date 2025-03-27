package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.List;
import java.util.UUID;

import com.oingmaryho.business.companyservice.domain.CompanyType;

public record CompanySearchResponseServiceDto(
	UUID id,
	String name,
	CompanyType type,
	Long managerId,
	UUID manageHubId,
	String address,
	Boolean isDeleted
) {
}
