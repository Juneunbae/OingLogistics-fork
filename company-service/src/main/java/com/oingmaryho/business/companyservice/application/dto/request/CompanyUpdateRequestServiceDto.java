package com.oingmaryho.business.companyservice.application.dto.request;

import java.util.UUID;

public record CompanyUpdateRequestServiceDto(
	UUID id,
	String name,
	String type,
	UUID manageHubId,
	String address
) {
}
