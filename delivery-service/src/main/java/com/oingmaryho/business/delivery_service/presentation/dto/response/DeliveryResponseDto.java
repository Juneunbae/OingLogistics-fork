package com.oingmaryho.business.delivery_service.presentation.dto.response;

import java.util.List;

public record DeliveryResponseDto(int page,
                                  int size,
                                  int sortDirection,
                                  List<DeliveryDetailResponseDto> deliveries){
}
