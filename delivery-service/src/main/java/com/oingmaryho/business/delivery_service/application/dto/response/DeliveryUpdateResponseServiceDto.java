package com.oingmaryho.business.delivery_service.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DeliveryUpdateResponseServiceDto {
    private UUID id;
}
