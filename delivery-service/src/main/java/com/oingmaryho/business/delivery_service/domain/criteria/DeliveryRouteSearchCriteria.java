package com.oingmaryho.business.delivery_service.domain.criteria;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryRouteSearchCriteria {

    private final UUID routeId;
    private final UUID deliveryId;
    private final UUID departureHubId;
    private final UUID arriveHubId;
    private final UUID companyId;
    private final Long managerId;
    private final DeliveryRouteStatus status;
    private final Boolean isDeleted;

}
