package com.oingmaryho.business.delivery_service.domain.criteria;

import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class DeliveryManagerSearchCriteria {
    private final UUID id;
    private final String slackId;
    private final UUID hubId;
    private final Long managerId;
    private final DeliveryManagerType type;
    private final Integer sequence;
    private final Boolean isDeleted;
}
