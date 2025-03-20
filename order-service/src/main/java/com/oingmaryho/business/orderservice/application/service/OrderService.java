package com.oingmaryho.business.orderservice.application.service;

import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.OrdersRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final CacheManager cacheManager;
    private final OrderJPARepository orderJPARepository;
    private final OrderApplicationMapper orderApplicationMapper;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orders")
    public Page<OrderResponseServiceDto> getOrders(OrdersRequestServiceDto ordersRequestServiceDto) {
        // TODO: Role - HubManager, HubDeliveryManager, CompanyDeliveryManager, CompanyManager 설정하기

        Pageable customPageable = ordersRequestServiceDto.customPageable();
        Page<Order> orders = orderJPARepository.findAll(customPageable);
        // TODO: QueryDSL 반영하여 LIKE 문 수정하기

        List<OrderResponseServiceDto> ordersDto = orders.stream().map(
            order -> orderApplicationMapper.toOrderResponseServiceDto(
                order,
                order.getOrderDetails().stream().map(
                    orderDetail -> orderApplicationMapper.toOrderDetailDto(order.getId(), orderDetail)
                ).toList()
            )
        ).toList();

        return new PageImpl<>(ordersDto, customPageable, orders.getTotalElements());
    }

    public Order getByOrderId(UUID orderId) {
        return orderJPARepository.findById(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));
    }

    public OrderDetail getByOrderDetailId(Order order, UUID orderDetailId) {
        return order.getOrderDetails().stream().filter(
            orderDetail -> orderDetail.getId().equals(orderDetailId)
        ).findFirst().orElseThrow(() -> new OrderException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
    }

    private Page<OrderResponseServiceDto> getOrdersCache(String cacheKey) {
        Cache cache = cacheManager.getCache("orders");
        return cache.get(cacheKey, Page.class);
    }

    private void putOrdersCache(String cacheKey, Page<OrderResponseServiceDto> results) {
        Cache cache = cacheManager.getCache("orders");
        cache.put(cacheKey, results);
        log.info("캐시 저장 성공: {}", cacheKey);
    }
}