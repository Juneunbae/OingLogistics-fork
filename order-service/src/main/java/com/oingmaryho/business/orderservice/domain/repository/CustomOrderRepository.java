package com.oingmaryho.business.orderservice.domain.repository;

import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomOrderRepository {
    Page<Order> findDynamicQuery(OrderSearchCriteria criteria, Pageable pageable);
}