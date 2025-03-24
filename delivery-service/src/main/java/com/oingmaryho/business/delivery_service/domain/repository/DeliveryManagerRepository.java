package com.oingmaryho.business.delivery_service.domain.repository;

import com.oingmaryho.business.delivery_service.domain.entity.DeliveryManager;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DeliveryManagerRepository extends JpaRepository<DeliveryManager, UUID> {
    Optional<DeliveryManager> findByManagerIdAndIsDeletedFalse(Long managerId);
    Optional<DeliveryManager> findByTypeAndSequence(DeliveryManagerType type, Integer sequence);
    Optional<DeliveryManager> findByHubIdAndTypeAndSequence(UUID hubId, DeliveryManagerType type, Integer sequence);
}
