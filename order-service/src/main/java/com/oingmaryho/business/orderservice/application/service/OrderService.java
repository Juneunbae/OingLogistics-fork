package com.oingmaryho.business.orderservice.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.HubSearchResponseDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailUpdateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.event.OrderEvent;
import com.oingmaryho.business.orderservice.application.service.feignclient.CompanyClient;
import com.oingmaryho.business.orderservice.application.service.feignclient.HubClient;
import com.oingmaryho.business.orderservice.application.service.feignclient.ProductClient;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.domain.OrderSearchCriteria;
import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderQueryRepository;
import com.oingmaryho.business.orderservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.ProductDetailsSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final HubClient hubClient;
    private final CacheManager cacheManager;
    private final CompanyClient companyClient;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderApplicationMapper orderApplicationMapper;

    @Value("${message.queue.product}")
    private String queueProduct;

    @Value("${message.queue.err.product}")
    private String queueErrProduct;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orders")
    public Page<OrderResponseServiceDto> getOrders(Long userId, String role, OrdersRequestServiceDto ordersRequestServiceDto) {
        Pageable customPageable = ordersRequestServiceDto.customPageable();

        Page<Order> orders = checkRole(userId, role, ordersRequestServiceDto, customPageable);

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

    @Transactional(readOnly = true)
    public OrderResponseServiceDto getOrder(Long userId, String role, OrderRequestServiceDto orderRequestServiceDto) {
        UUID orderId = orderRequestServiceDto.orderId();
        Order order = getByOrderId(orderId);

        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            HubSearchResponseDto hubInfo = hubClient.getHubById(userId);

            UUID hubId = hubInfo.id();
            if (!order.getRequesterId().equals(hubId)) {
                evictCache(order);
                throw new OrderException(ErrorCode.HUB_NOT_MATCH);
            }
        }

        if (
            Objects.equals(role, UserRoleType.COMPANY_MANAGER.name())
                || Objects.equals(role, UserRoleType.HUB_DELIVERY_MANAGER.name())
                || Objects.equals(role, UserRoleType.COMPANY_DELIVERY_MANAGER.name())
        ) {
            if (!order.getRequesterUserId().equals(userId)) {
                evictCache(order);
                throw new OrderException(ErrorCode.FORBIDDEN);
            }
        }

        return orderApplicationMapper.toOrderResponseServiceDto(
            order,
            order.getOrderDetails().stream().map(
                orderDetail -> orderApplicationMapper.toOrderDetailDto(order.getId(), orderDetail)
            ).toList()
        );
    }

    @Transactional
    public void createOrder(OrderCreateRequestServiceDto create) {
        int totalPrice = 0;
        ArrayList<OrderDetail> details = new ArrayList<>();

        CompanyDetailsSearchResponseDto requestCompanyInfo = getCompanyInfo(create.requesterId());

        Order order = Order.builder()
            .requesterId(requestCompanyInfo.id())
            .requesterSlackId(create.slackId())
            .requesterName(requestCompanyInfo.name())
            .requesterAddress(requestCompanyInfo.address())
            .requesterHubId(requestCompanyInfo.manageHubId())
            .requesterUserId(create.userId())
            .requesterUsername(create.username())
            .status(Status.ORDERING)
            .requests(create.requests())
            .totalPrice(totalPrice)
            .isDeleted(false)
            .createdAt(LocalDateTime.now())
            .createdBy(create.userId())
            .build();

        // orderId, requesterId, requesterName, productId, quantity
        for (OrderDetailCreateRequestServiceDto orderDetail : create.orderDetails()) {
            ProductDetailsSearchResponseDto productInfo = getProductInfo(orderDetail.productId());

            log.info(productInfo.toString());
            log.info(orderDetail.toString());

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

        orderRepository.save(order);
        publisher.publishEvent(new OrderEvent(order));
    }

    @Transactional
    public void updateOrder(Long userId, String role, OrderUpdateServiceDto update) {
        int totalPrice = 0;
        UUID orderId = update.id();

        Order order = getByOrderId(orderId);

        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            HubSearchResponseDto hubInfo = hubClient.getHubById(userId);

            UUID hubId = hubInfo.id();
            if (!order.getRequesterId().equals(hubId)) {
                throw new OrderException(ErrorCode.HUB_NOT_MATCH);
            }
        }

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
        log.info("주문 수정 완료");

        refreshCache(order);
    }

    @Transactional
    public void deleteOrder(Long userId, String role, OrderDeleteServiceDto delete) {
        UUID orderId = delete.orderId();

        Order order = getByOrderId(orderId);

        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            HubSearchResponseDto hubInfo = hubClient.getHubById(userId);

            UUID hubId = hubInfo.id();
            if (!order.getRequesterId().equals(hubId)) {
                throw new OrderException(ErrorCode.HUB_NOT_MATCH);
            }
        }

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            orderDetail.softDeleted(userId);
            log.info("상세 주문 삭제 완료");
        }

        order.softDeleted(userId);
        log.info("주문 삭제 완료");

        evictCache(order);
    }

    @Transactional
    public void deleteOrderDetail(Long userId, String role, OrderDetailDeleteRequestServiceDto request) {
        UUID orderId = request.orderId();

        Order order = getByOrderId(orderId);

        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            HubSearchResponseDto hubInfo = hubClient.getHubById(userId);

            UUID hubId = hubInfo.id();
            if (!order.getRequesterId().equals(hubId)) {
                throw new OrderException(ErrorCode.HUB_NOT_MATCH);
            }
        }

        OrderDetail orderDetail = getByOrderDetailId(order, request.orderDetailId());

        orderDetail.softDeleted(userId);
        log.info("주문: {}, 상세 주문: {}, 삭제 완료", order.getId(), orderDetail.getId());

        Integer orderDetailPrice = (orderDetail.getQuantity() * orderDetail.getPrice());

        order.updateTotalPrice(orderDetailPrice);

        evictCache(order);
    }

    private Order getByOrderId(UUID orderId) {
        Cache cache = cacheManager.getCache("order");
        Order cachedOrder = cache.get(orderId, Order.class);

        if (cachedOrder != null) {
            log.info("캐시된 주문 조회 반환 성공");
            return cachedOrder;
        }

        Order order = orderRepository.findByIdAndIsDeletedIsFalse(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));

        cache.put(orderId, order);
        log.info("주문 캐시 저장 완료");

        return order;
    }

    private OrderDetail getByOrderDetailId(Order order, UUID orderDetailId) {
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

    private CompanyDetailsSearchResponseDto getCompanyInfo(UUID companyId) {
        return companyClient.getCompanyId(companyId)
            .orElseThrow(() -> new OrderException(ErrorCode.COMPANY_NOT_FOUND));
    }

    private ProductDetailsSearchResponseDto getProductInfo(UUID productId) {
        return productClient.getProductById(productId)
            .orElseThrow(() -> new OrderException(ErrorCode.PRODUCT_NOT_FOUND));
    }

    private HubSearchResponseDto getHubInfo(Long managerId) {
        return hubClient.getHubById(managerId);
    }

    private OrderSearchCriteria createOrderSearchCriteria(OrdersRequestServiceDto requestDto) {
        return OrderSearchCriteria.builder()
            .productName(requestDto.productName())
            .recipientName(requestDto.recipientName())
            .requesterName(requestDto.requesterName())
            .isDeleted(requestDto.isDeleted())
            .build();
    }

    private Page<Order> checkRole(Long userId, String role, OrdersRequestServiceDto ordersRequestServiceDto, Pageable customPageable) {
        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            log.info("Role check success - {}", role);

            HubSearchResponseDto hubInfo = getHubInfo(userId);
            UUID hubId = hubInfo.id();

            return orderQueryRepository.findDynamicQueryForHubManager(
                createOrderSearchCriteria(ordersRequestServiceDto),
                customPageable,
                hubId
            );
        } else if (
            Objects.equals(role, UserRoleType.COMPANY_MANAGER.name())
                || Objects.equals(role, UserRoleType.COMPANY_DELIVERY_MANAGER.name())
                || Objects.equals(role, UserRoleType.HUB_DELIVERY_MANAGER.name())
        ) {
            log.info("Role check success - {}", role);
            return orderQueryRepository.findDynamicQueryForOther(
                createOrderSearchCriteria(ordersRequestServiceDto),
                customPageable,
                userId
            );
        }

        return new PageImpl<>(Collections.emptyList(), customPageable, 0);
    }
}