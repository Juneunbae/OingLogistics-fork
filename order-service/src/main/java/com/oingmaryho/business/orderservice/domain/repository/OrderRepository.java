package com.oingmaryho.business.orderservice.domain.repository;

import com.oingmaryho.business.orderservice.domain.Order;

import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    Order save(Order order);

    Optional<Order> findByIdAndIsDeletedIsFalse(UUID id);
}