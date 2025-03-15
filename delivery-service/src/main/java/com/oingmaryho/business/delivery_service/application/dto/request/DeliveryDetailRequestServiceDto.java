package com.oingmaryho.business.delivery_service.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryDetailRequestServiceDto {
    private UUID id;
}
