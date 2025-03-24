package com.oingmaryho.business.companyservice.application.dto.request;

import java.util.UUID;

public record CompanyProductDeleteRequestDto(
	UUID companyId,
	Long userId
) {
}