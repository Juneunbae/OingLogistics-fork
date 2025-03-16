package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.List;

public record DeliveryResponseServiceDto(int page,
                                         int size,
                                         int sortDirection,
                                         List<DeliveryDetailResponseServiceDto> deliveries){
}
