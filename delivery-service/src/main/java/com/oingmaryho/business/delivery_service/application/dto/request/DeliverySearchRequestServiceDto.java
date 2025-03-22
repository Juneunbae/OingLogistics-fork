package com.oingmaryho.business.delivery_service.application.dto.request;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record DeliverySearchRequestServiceDto(
        UUID id,
        UUID orderId,
        UUID hubId,
        UUID companyId,
        DeliveryStatus status,
        Long managerId,
        Boolean isDeleted,
        Pageable customPageable){
}
