package com.oingmaryho.business.companyservice.presentation.dto.request;

import java.util.UUID;

public record CompanyUpdateRequestDto(
	UUID id,
	String name,
	String type,
	Long managerId,
	UUID manageHubId,
	String address
) {
}
