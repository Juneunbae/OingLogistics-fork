package com.oingmaryho.business.companyservice.presentation.dto.request;

import java.util.UUID;

public record CompanyCreateRequestDto(
	String name,
	String type,
	UUID manageHubId,
	String address

) {
}
