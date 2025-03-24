package com.oingmaryho.business.companyservice.application.dto.request;

import java.util.UUID;

import org.springframework.data.domain.Pageable;

public record CompanySearchRequestServiceDto(
	UUID id,
	String type,
	String name,
	Long managerId,
	UUID manageHubId,
	String address,
	Boolean isDeleted
) {
}
