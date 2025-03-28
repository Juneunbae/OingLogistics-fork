package com.oingmaryho.business.orderservice.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.OrderCreateRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.request.OrderDetailCreateRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.request.ProductQueueRequestDto;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderHelper {
    private final HubClient hubClient;
    private final CacheManager cacheManager;
    private final CompanyClient companyClient;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;
    private final OrderJPARepository orderJPARepository;
    private final OrderApplicationMapper orderApplicationMapper;

    @Value("${message.queue.product}")
    private String queueProduct;

    @Value("${message.queue.err.product}")
    private String queueErrProduct;

    public Order getByOrderId(UUID orderId) {
        Cache cache = cacheManager.getCache("order");
        Order cachedOrder = cache.get(orderId, Order.class);

        if (cachedOrder != null) {
            return cachedOrder;
        }

        Order order = orderJPARepository.findById(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));

        cache.put(orderId, order);

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
            cache.put(order.getId(), order);
        }

        log.info("캐시 재할당 완료: {}", order.getId());
    }

    public void evictCache(Order order) {
        Cache OrdersCache = cacheManager.getCache("orders");
        Cache OrderCache = cacheManager.getCache("order");

        if (OrderCache != null) {
            OrderCache.evict(order.getId());
        }

        OrdersCache.clear();
    }

    public Order createOrderDetails(OrderCreateRequestServiceDto create, Order order, int totalPrice) {
        ArrayList<OrderDetail> details = new ArrayList<>();

        for (OrderDetailCreateRequestServiceDto orderDetail : create.orderDetails()) {
            ProductDetailsSearchResponseDto productInfo = this.getProductInfo(orderDetail.productId());

            if (!orderDetail.recipientId().equals(productInfo.companyId())) {
                throw new OrderException(ErrorCode.COMPANY_NOT_MATCH);
            }

            ProductQueueRequestDto QueueRequest = orderApplicationMapper.toProductQueueRequestDto(
                productInfo.id(),
                orderDetail.quantity()
            );

            Object response = rabbitTemplate.convertSendAndReceive(queueProduct, QueueRequest);

            if (response == null) {
                rabbitTemplate.convertAndSend(queueErrProduct, QueueRequest);
                throw new OrderException(ErrorCode.PRODUCT_SERVER_ERROR);
            }

            try {
                String responseString = new String((byte[]) response, StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                int statusCode = objectMapper.readValue(responseString, Integer.class);

                log.info(String.valueOf(statusCode));
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            totalPrice += (productInfo.price() * orderDetail.quantity());

            OrderDetail detail = OrderDetail.builder()
                .order(order)
                .recipientId(productInfo.companyId())
                .recipientName(productInfo.companyName())
                .recipientHubId(productInfo.manageHubId())
                .productId(productInfo.id())
                .productName(productInfo.name())
                .quantity(orderDetail.quantity())
                .price(productInfo.price())
                .isDeleted(false)
                .createdAt(LocalDateTime.now())
                .createdBy(create.userId())
                .build();

            details.add(detail);
        }
        order.inputTotalPrice(totalPrice);
        order.addOrderDetail(details);

        return order;
    }
}