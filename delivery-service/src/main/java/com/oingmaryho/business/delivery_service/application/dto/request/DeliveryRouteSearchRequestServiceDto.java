package com.oingmaryho.business.delivery_service.application.dto.request;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record DeliveryRouteSearchRequestServiceDto(UUID id, // 배송 id
                                                   UUID hubId,
                                                   UUID companyId,
                                                   UUID managerId,
                                                   Pageable customPageable){
}
