package com.oingmaryho.business.delivery_service.application.feign;

import java.util.UUID;

public record HubSearchResponseDto(
        UUID id,
        String name,
        String address,
        Double latitude,
        Double longitude,
        Long managerId
){}
