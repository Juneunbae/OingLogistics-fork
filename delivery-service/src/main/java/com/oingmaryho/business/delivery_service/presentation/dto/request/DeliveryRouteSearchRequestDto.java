package com.oingmaryho.business.delivery_service.presentation.dto.request;

import lombok.Getter;

import java.util.UUID;

@Getter
public class DeliveryRouteSearchRequestDto {
    private UUID hubId;
    private UUID companyId;
    private Long managerId;
}
