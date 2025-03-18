package com.oingmaryho.business.delivery_service.infrastructure;

import com.oingmaryho.business.delivery_service.domain.Delivery;
import com.oingmaryho.business.delivery_service.domain.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.domain.DeliveryRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryCustomRepository {
    Page<Delivery> searchDelivery(UUID hubId, UUID companyId, UUID managerId, DeliveryManagerType managerType, Pageable pageable);
    Page<DeliveryRoute> searchRoute(UUID hubId, UUID companyId, UUID managerId, DeliveryManagerType managerType, Pageable pageable);
    Optional<DeliveryRoute> findByRouteId(UUID routeId);
}
