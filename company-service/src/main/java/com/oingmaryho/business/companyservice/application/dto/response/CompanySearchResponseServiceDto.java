package com.oingmaryho.business.companyservice.application.dto.response;

import java.util.List;
import java.util.UUID;

public record CompanySearchResponseServiceDto(
	UUID id,
	String name,
	String type,
	Long managerId,
	UUID manageHubId,
	String address,
	Boolean isDeleted
) {
}
