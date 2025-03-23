package com.oingmaryho.business.delivery_service.domain.criteria;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliverySearchCriteria {

    private final UUID id;
    private final UUID orderId;
    private final UUID orderDetailId;
    private final UUID hubId;
    private final UUID companyId;
    private final DeliveryStatus status;
    private final Long managerId;
    private final Boolean isDeleted;

}
