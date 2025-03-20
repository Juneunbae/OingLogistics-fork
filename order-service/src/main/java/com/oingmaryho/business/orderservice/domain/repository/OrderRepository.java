package com.oingmaryho.business.orderservice.domain.repository;

import com.oingmaryho.business.orderservice.domain.Order;

public interface OrderRepository {
    Order save(Order order);
}