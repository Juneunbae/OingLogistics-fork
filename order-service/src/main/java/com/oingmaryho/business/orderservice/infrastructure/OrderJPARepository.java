package com.oingmaryho.business.orderservice.infrastructure;

import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrderJPARepository extends JpaRepository<Order, UUID>, OrderRepository {
}