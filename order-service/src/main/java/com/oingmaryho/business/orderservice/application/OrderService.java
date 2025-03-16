package com.oingmaryho.business.orderservice.application;

import com.oingmaryho.business.orderservice.application.dto.OrderServiceDto;
import com.oingmaryho.business.orderservice.application.dto.OrdersServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.infrastructure.OrderRepository;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final CacheManager cacheManager;
    private final OrderRepository orderRepository;
    private final OrderApplicationMapper orderApplicationMapper;

    public Page<OrderDto> getOrders(OrdersServiceDto ordersServiceDto) {
        // TODO: 마스터 확인

        String cacheKey = makeOrdersCacheKey(ordersServiceDto);

        Page<OrderDto> cachedOrders = getOrdersCache(cacheKey);
        if (cachedOrders != null) {
            log.info("캐시된 주문 전체 조회 반환 성공");
            return cachedOrders;
        }

        Pageable customPageable = ordersServiceDto.customPageable();
        Page<Order> orders = orderRepository.findAll(customPageable);

        List<OrderDto> ordersDto = orders.stream().map(
            order -> orderApplicationMapper.toOrderDto(
                order,
                order.getOrderDetails().stream().map(
                    orderApplicationMapper::toOrderDetailDto
                ).toList()
            )
        ).toList();

        Page<OrderDto> results = new PageImpl<>(ordersDto, customPageable, orders.getTotalElements());

        putOrdersCache(cacheKey, results);

        return results;
    }

    public Order getById(OrderServiceDto orderServiceDto) {
        return orderRepository.findById(orderServiceDto.orderId())
            // TODO: 주문 에러 메시지 추가
            .orElseThrow();
    }

    private String makeOrdersCacheKey(OrdersServiceDto ordersServiceDto) {
        String productName = ordersServiceDto.productName();
        productName = (productName == null) ? "" : productName;

        String recipientName = ordersServiceDto.recipientName();
        recipientName = (recipientName == null) ? "" : recipientName;

        String requesterName = ordersServiceDto.requesterName();
        requesterName = (requesterName == null) ? "" : requesterName;

        Boolean isDeleted = ordersServiceDto.isDeleted();
        isDeleted = isDeleted != null && isDeleted;

        Pageable customPageable = ordersServiceDto.customPageable();
        int customPageableHashCode = (customPageable != null) ? customPageable.hashCode() : 0;

        return "Orders_" +
            productName.hashCode() +
            recipientName.hashCode() +
            requesterName.hashCode() +
            isDeleted.hashCode() +
            customPageableHashCode;
    }

    private Page<OrderDto> getOrdersCache(String cacheKey) {
        Cache cache = cacheManager.getCache("orders");
        return cache.get(cacheKey, Page.class);
    }

    private void putOrdersCache(String cacheKey, Page<OrderDto> results) {
        Cache cache = cacheManager.getCache("orders");
        cache.put(cacheKey, results);
        log.info("캐시 저장 성공: {}", cacheKey);
    }
}