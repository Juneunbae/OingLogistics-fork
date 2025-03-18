package com.oingmaryho.business.delivery_service.infrastructure;

import com.oingmaryho.business.delivery_service.domain.Delivery;
import com.oingmaryho.business.delivery_service.domain.DeliveryManagerType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DeliveryCustomRepository {
    Page<Delivery> searchDelivery(UUID hubId, UUID companyId, UUID managerId, DeliveryManagerType managerType, Pageable pageable);
}
