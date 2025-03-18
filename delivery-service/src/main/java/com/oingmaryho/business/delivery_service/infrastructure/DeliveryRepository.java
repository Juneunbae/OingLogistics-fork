package com.oingmaryho.business.delivery_service.infrastructure;

import com.oingmaryho.business.delivery_service.domain.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DeliveryRepository extends JpaRepository<Delivery, UUID>, DeliveryCustomRepository {

}
