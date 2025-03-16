package com.oingmaryho.business.delivery_service.presentation.dto.request;

import java.util.UUID;

public record DeliverySearchRequestDto(UUID hubId,
                                       UUID companyId,
                                       Long managerId){
}
