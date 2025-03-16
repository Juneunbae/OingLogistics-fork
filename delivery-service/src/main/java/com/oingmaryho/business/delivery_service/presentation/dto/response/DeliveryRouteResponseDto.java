package com.oingmaryho.business.delivery_service.presentation.dto.response;

import java.util.List;

public record DeliveryRouteResponseDto(int page,
                                       int size,
                                       int sortDirection,
                                       List<DeliveryRouteDetailResponseDto> routes){
}
