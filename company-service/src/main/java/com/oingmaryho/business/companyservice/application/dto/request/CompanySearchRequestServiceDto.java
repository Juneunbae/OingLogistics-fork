package com.oingmaryho.business.companyservice.application.dto.request;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

import com.oingmaryho.business.companyservice.domain.CompanyType;

public record CompanySearchRequestServiceDto(
	UUID id,
	CompanyType type,
	String name,
	Long managerId,
	UUID manageHubId,
	String address,
	Boolean isDeleted
) {
}
