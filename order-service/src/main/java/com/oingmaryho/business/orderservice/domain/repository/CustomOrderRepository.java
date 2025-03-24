package com.oingmaryho.business.orderservice.domain.repository;

import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface CustomOrderRepository {
    Page<Order> findDynamicQuery(OrderSearchCriteria criteria, Pageable pageable);

    Page<Order> findDynamicQueryForHubManager(OrderSearchCriteria criteria, Pageable pageable, UUID hubId);

    Page<Order> findDynamicQueryForOther(OrderSearchCriteria criteria, Pageable pageable, Long userId);
}