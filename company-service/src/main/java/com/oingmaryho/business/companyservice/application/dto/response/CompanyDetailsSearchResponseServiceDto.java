package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.UUID;

public record CompanyDetailsSearchResponseServiceDto(
	UUID id,
	String name,
	String type,
	Long managerId,
	UUID manageHubId,
	String address
) {
}
