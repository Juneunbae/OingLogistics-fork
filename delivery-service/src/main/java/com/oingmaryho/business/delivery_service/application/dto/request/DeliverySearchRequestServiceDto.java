package com.oingmaryho.business.delivery_service.application.dto.request;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record DeliverySearchRequestServiceDto(
        Long userId,
        UUID hubId,
        UUID companyId,
        UUID managerId,
        Pageable customPageable){
}
