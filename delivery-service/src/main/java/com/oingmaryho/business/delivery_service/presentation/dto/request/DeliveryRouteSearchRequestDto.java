package com.oingmaryho.business.delivery_service.presentation.dto.request;

import java.util.UUID;

public record DeliveryRouteSearchRequestDto(UUID hubId,
                                            UUID companyId,
                                            UUID managerId){
}
