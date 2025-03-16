package com.oingmaryho.business.delivery_service.application.dto.response;

import java.util.List;

public record DeliveryRouteResponseServiceDto(int page,
                                              int size,
                                              int sortDirection,
                                              List<DeliveryRouteDetailResponseServiceDto> routes){

}
