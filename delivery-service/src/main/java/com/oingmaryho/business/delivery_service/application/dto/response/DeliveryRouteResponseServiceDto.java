package com.oingmaryho.business.delivery_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryRouteResponseServiceDto {
    private int page;
    private int size;
    private int sortDirection;
    private List<DeliveryRouteDetailResponseServiceDto> routes;
}
