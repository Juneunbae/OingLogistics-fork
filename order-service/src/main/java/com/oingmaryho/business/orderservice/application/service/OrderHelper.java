package com.oingmaryho.business.orderservice.application.service;

import com.oingmaryho.business.orderservice.application.dto.response.HubSearchResponseDto;
import com.oingmaryho.business.orderservice.application.service.feignclient.CompanyClient;
import com.oingmaryho.business.orderservice.application.service.feignclient.HubClient;
import com.oingmaryho.business.orderservice.application.service.feignclient.ProductClient;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderJPARepository;
import com.oingmaryho.business.orderservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderAdminResponseServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.ProductDetailsSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderHelper {
    private final HubClient hubClient;
    private final CacheManager cacheManager;
    private final CompanyClient companyClient;
    private final ProductClient productClient;
    private final OrderJPARepository orderJPARepository;

    public Order getByOrderId(UUID orderId) {
        Cache cache = cacheManager.getCache("order");
        Order cachedOrder = cache.get(orderId, Order.class);

        if (cachedOrder != null) {
            log.info("캐시된 주문 조회 반환 성공");
            return cachedOrder;
        }

        Order order = orderJPARepository.findById(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));

        cache.put(orderId, order);
        log.info("주문 캐시 저장 완료");

        return order;
    }

    public OrderDetail getByOrderDetailId(Order order, UUID orderDetailId) {
        return order.getOrderDetails().stream().filter(
            orderDetail -> orderDetail.getId().equals(orderDetailId) && !orderDetail.getIsDeleted()
        ).findFirst().orElseThrow(() -> new OrderException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
    }

    public CompanyDetailsSearchResponseDto getCompanyInfo(UUID companyId) {
        return companyClient.getCompanyId(companyId)
            .orElseThrow(() -> new OrderException(ErrorCode.COMPANY_NOT_FOUND));
    }

    public ProductDetailsSearchResponseDto getProductInfo(UUID productId) {
        return productClient.getProductById(productId)
            .orElseThrow(() -> new OrderException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    public HubSearchResponseDto getHubInfo(Long managerId) {
        return hubClient.getHubById(managerId);
    }

    public Page<OrderAdminResponseServiceDto> getOrdersCache(String cacheKey) {
        Cache cache = cacheManager.getCache("orders");
        return cache.get(cacheKey, Page.class);
    }

    public void refreshCache(Order order) {
        Cache cache = cacheManager.getCache("order");

        if (cache != null) {
            cache.evict(order.getId());
            log.info("Refresh - 캐시 삭제");

            cache.put(order.getId(), order);
            log.info("Refresh - 캐시 할당");
        }

        log.info("캐시 재할당 완료: {}", order.getId());
    }

    public void evictCache(Order order) {
        Cache OrdersCache = cacheManager.getCache("orders");
        Cache OrderCache = cacheManager.getCache("order");

        if (OrderCache != null) {
            OrderCache.evict(order.getId());
            log.info("Order - 캐시 삭제");
        }

        OrdersCache.clear();
    }
}