package com.oingmaryho.business.orderservice.application.service;

import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailUpdateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
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
public class OrderAdminService {
    private final CacheManager cacheManager;
    private final OrderRepository orderRepository;
    private final OrderApplicationMapper orderApplicationMapper;

    @Transactional
    public Page<OrderResponseServiceDto> getOrders(OrdersRequestServiceDto ordersRequestServiceDto) {
        // TODO: 마스터 권한 검증

        String cacheKey = makeOrdersCacheKey(ordersRequestServiceDto);

        Page<OrderResponseServiceDto> cachedOrders = getOrdersCache(cacheKey);
        if (cachedOrders != null) {
            log.info("캐시된 주문 전체 조회 반환 성공");
            return cachedOrders;
        }

        Pageable customPageable = ordersRequestServiceDto.customPageable();
        Page<Order> orders = orderRepository.findAll(customPageable);
        // TODO: QueryDSL 반영하여 LIKE 문 수정하기

        List<OrderResponseServiceDto> ordersDto = orders.stream().map(
            order -> orderApplicationMapper.toOrderResponseServiceDto(
                order,
                order.getOrderDetails().stream().map(
                    orderDetail -> orderApplicationMapper.toOrderDetailDto(order.getId(), orderDetail)
                ).toList()
            )
        ).toList();

        Page<OrderResponseServiceDto> results = new PageImpl<>(ordersDto, customPageable, orders.getTotalElements());

        putOrdersCache(cacheKey, results);

        return results;
    }

    @Transactional
    public OrderResponseServiceDto getOrder(OrderRequestServiceDto orderRequestServiceDto) {
        // TODO: 마스터 권한 검증

        UUID orderId = orderRequestServiceDto.orderId();
        Order order = getByOrderId(orderId);

        return orderApplicationMapper.toOrderResponseServiceDto(
            order,
            order.getOrderDetails().stream().map(
                orderDetail -> orderApplicationMapper.toOrderDetailDto(order.getId(), orderDetail)
            ).toList()
        );
    }

    @Transactional
    public void updateOrder(OrderUpdateServiceDto update) {
        // TODO: 마스터 권한 검증
        int totalPrice = 0;
        UUID orderId = update.id();

        Order order = getByOrderId(orderId);

        if (update.orderDetails() != null) {
            for (OrderDetailUpdateResponseServiceDto orderDetailDto : update.orderDetails()) {
                OrderDetail orderDetail = getByOrderDetailId(order, orderDetailDto.orderDetailId());
                OrderDetailUpdateRequestServiceDto orderDetailUpdateRequestServiceDto = orderApplicationMapper.toOrderDetailUpdateDto(
                    orderDetailDto.price(), orderDetailDto.quantity()
                );
                orderDetail.update(orderDetailUpdateRequestServiceDto);

                log.info("상세 주문: {}, 수정 완료", orderDetail.getId());
            }

            totalPrice = order.getOrderDetails().stream().mapToInt(
                orderDetail -> orderDetail.getPrice() * orderDetail.getQuantity()
            ).sum();
        }

        OrderUpdateRequestServiceDto orderUpdateRequestServiceDto = orderApplicationMapper.toOrderUpdateDto(update.requests(), totalPrice);

        order.update(orderUpdateRequestServiceDto);
        orderRepository.save(order);
        log.info("주문 수정 완료");

        refreshCache(order);
    }

    @Transactional
    public void deleteOrder(OrderDeleteServiceDto delete) {
        // TODO: 마스터 권한 검증

        UUID orderId = delete.orderId();

        Order order = getByOrderId(orderId);

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            orderDetail.delete();
            log.info("상세 주문 삭제 완료");
        }

        order.delete();
        log.info("주문 삭제 완료");

        evictCache(order);
    }

    @Transactional
    public void deleteOrderDetail(OrderDetailDeleteRequestServiceDto request) {
        // TODO: 마스터 권한 검증

        UUID orderId = request.orderId();

        Order order = getByOrderId(orderId);
        OrderDetail orderDetail = getByOrderDetailId(order, request.orderDetailId());

        orderDetail.delete();
        log.info("주문: {}, 상세 주문: {}, 삭제 완료", order.getId(), orderDetail.getId());

        Integer orderDetailPrice = (orderDetail.getQuantity() * orderDetail.getPrice());
        OrderTotalPriceUpdateRequestServiceDto updateDto = orderApplicationMapper.toOrderTotalPriceUpdateRequestDto(orderDetailPrice);

        order.updateTotalPrice(updateDto);
        orderRepository.save(order);

        evictCache(order);
    }

    public Order getByOrderId(UUID orderId) {
        Cache cache = cacheManager.getCache("order");
        Order cachedOrder = cache.get(orderId, Order.class);

        if (cachedOrder != null) {
            log.info("캐시된 주문 조회 반환 성공");
            return cachedOrder;
        }

        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));

        cache.put(orderId, order);
        log.info("주문 캐시 저장 완료");

        return order;
    }

    public OrderDetail getByOrderDetailId(Order order, UUID orderDetailId) {
        return order.getOrderDetails().stream().filter(
            orderDetail -> orderDetail.getId().equals(orderDetailId)
        ).findFirst().orElseThrow(() -> new OrderException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
    }

    private String makeOrdersCacheKey(OrdersRequestServiceDto ordersRequestServiceDto) {
        String productName = ordersRequestServiceDto.productName();
        productName = (productName == null) ? "" : productName;

        String recipientName = ordersRequestServiceDto.recipientName();
        recipientName = (recipientName == null) ? "" : recipientName;

        String requesterName = ordersRequestServiceDto.requesterName();
        requesterName = (requesterName == null) ? "" : requesterName;

        Boolean isDeleted = ordersRequestServiceDto.isDeleted();
        isDeleted = isDeleted != null && isDeleted;

        Pageable customPageable = ordersRequestServiceDto.customPageable();
        int customPageableHashCode = (customPageable != null) ? customPageable.hashCode() : 0;

        return "Orders_" +
            productName.hashCode() +
            recipientName.hashCode() +
            requesterName.hashCode() +
            isDeleted.hashCode() +
            customPageableHashCode;
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

    private void refreshCache(Order order) {
        Cache cache = cacheManager.getCache("order");

        if (cache != null) {
            cache.evict(order.getId());
            log.info("Refresh - 캐시 삭제");

            cache.put(order.getId(), order);
            log.info("Refresh - 캐시 할당");
        }

        log.info("캐시 재할당 완료: {}", order.getId());
    }

    private void evictCache(Order order) {
        Cache OrdersCache = cacheManager.getCache("orders");
        Cache OrderCache = cacheManager.getCache("order");

        if (OrderCache != null) {
            OrderCache.evict(order.getId());
            log.info("Order - 캐시 삭제");
        }

        OrdersCache.clear();
    }
}