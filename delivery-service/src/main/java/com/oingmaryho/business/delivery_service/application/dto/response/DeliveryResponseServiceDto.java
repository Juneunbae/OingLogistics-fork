package com.oingmaryho.business.delivery_service.application.dto.response;

import org.springframework.data.domain.Pageable;

import java.util.List;

public record DeliveryResponseServiceDto(Pageable customPageable,
                                         List<DeliveryDetailResponseServiceDto> deliveries){
}
