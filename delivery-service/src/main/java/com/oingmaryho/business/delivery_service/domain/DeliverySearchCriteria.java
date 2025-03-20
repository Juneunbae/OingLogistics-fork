package com.oingmaryho.business.delivery_service.domain;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliverySearchCriteria {

    private final UUID hubId;
    private final UUID companyId;
    private final UUID managerId;
    private final DeliveryManagerType managerType;
    private final Boolean isDeleted;

}
