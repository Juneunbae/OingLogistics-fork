package com.oingmaryho.business.delivery_service.application.dto.request;

import java.util.UUID;

public record DeliverySearchRequestServiceDto(UUID hubId,
                                             UUID companyId,
                                             Long managerId){
}
