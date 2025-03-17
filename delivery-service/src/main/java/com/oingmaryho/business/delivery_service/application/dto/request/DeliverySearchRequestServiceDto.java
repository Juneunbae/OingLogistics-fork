package com.oingmaryho.business.delivery_service.application.dto.request;

import org.springframework.data.domain.Pageable;

import java.util.UUID;

public record DeliverySearchRequestServiceDto(UUID hubId,
                                              UUID companyId,
                                              Long managerId,
                                              Pageable customPageable){
}
