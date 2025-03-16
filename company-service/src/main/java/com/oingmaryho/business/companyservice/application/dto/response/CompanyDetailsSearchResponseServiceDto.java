package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.UUID;

public record CompanyDetailsSearchResponseServiceDto(
	UUID id,
	String name,
	String type,
	UUID manageHubId,
	String address
) {
}
