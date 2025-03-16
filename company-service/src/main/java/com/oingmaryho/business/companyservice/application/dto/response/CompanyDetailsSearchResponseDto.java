package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.UUID;

public record CompanyDetailsSearchResponseDto(
	UUID id,
	String name,
	String type,
	UUID manageHubId,
	String address
) {
}
