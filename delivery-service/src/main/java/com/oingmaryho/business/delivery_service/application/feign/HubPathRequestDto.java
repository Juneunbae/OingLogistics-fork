package com.oingmaryho.business.delivery_service.application.feign;

import java.util.UUID;

public record HubPathRequestDto(
        UUID departureHubId,
        String arriveAddress){
}
