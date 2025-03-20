package com.oingmaryho.business.delivery_service.infrastructure.repository;

import com.oingmaryho.business.delivery_service.domain.entity.Delivery;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryRoute;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID>, DeliveryCustomRepository {

    Optional<Delivery> findByIdAndIsDeletedFalse(UUID id);
}
