package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.UUID;

public record CompanyDetailsSearchResponseDto(
        UUID id,
        String name,
        Long managerId,
        String type,
        UUID manageHubId,
        String address){
}