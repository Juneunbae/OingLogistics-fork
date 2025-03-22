package com.oingmaryho.business.delivery_service.infrastructure.repository;

import com.oingmaryho.business.delivery_service.domain.criteria.DeliveryRouteSearchCriteria;
import com.oingmaryho.business.delivery_service.domain.criteria.DeliverySearchCriteria;
import com.oingmaryho.business.delivery_service.domain.entity.Delivery;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryManager;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryRoute;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryCustomRepository {
    Page<Delivery> searchDelivery(DeliverySearchCriteria criteria, Pageable pageable);
    Page<DeliveryRoute> searchRoute(DeliveryRouteSearchCriteria criteria, Pageable pageable);


    Optional<DeliveryRoute> findRouteById(UUID routeId);    // admin용
    Optional<DeliveryRoute> findRouteByIdAndIsDeleted(UUID routeId);

}
