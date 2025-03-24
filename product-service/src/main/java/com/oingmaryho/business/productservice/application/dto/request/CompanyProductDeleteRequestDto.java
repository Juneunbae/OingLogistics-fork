package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record CompanyProductDeleteRequestDto(
	UUID companyId,
	Long userId
) {
}
