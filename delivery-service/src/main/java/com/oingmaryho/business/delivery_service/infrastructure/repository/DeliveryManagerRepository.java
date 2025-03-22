package com.oingmaryho.business.delivery_service.infrastructure.repository;

import com.oingmaryho.business.delivery_service.domain.entity.DeliveryManager;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository extends JpaRepository<DeliveryManager, UUID> {
    Optional<DeliveryManager> findByManagerIdAndIsDeletedFalse(Long managerId);
}
