package com.oingmaryho.business.companyservice.application.dto.request;

import java.util.UUID;

public record CompanyUpdateRequestServiceDto(
	UUID id,
	String name,
	String type,
	Long managerId,
	UUID manageHubId,
	String address
) {
}
