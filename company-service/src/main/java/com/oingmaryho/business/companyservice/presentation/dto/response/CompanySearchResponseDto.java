package com.oingmaryho.business.companyservice.presentation.dto.response;

import java.util.List;
import java.util.UUID;

public record CompanySearchResponseDto(
	UUID id,
	String name,
	String type,
	Long managerId,
	UUID manageHubId,
	String address
) {

}
