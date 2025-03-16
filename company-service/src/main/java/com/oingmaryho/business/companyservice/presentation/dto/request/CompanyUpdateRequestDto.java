package com.oingmaryho.business.companyservice.presentation.dto.request;

import java.util.UUID;

public record CompanyUpdateRequestDto(
	String name,
	String type,
	UUID manageHubId,
	String address
) {
}
