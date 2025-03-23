package com.oingmaryho.business.orderservice.application.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailUpdateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.event.OrderEvent;
import com.oingmaryho.business.orderservice.application.service.feignclient.CompanyClient;
import com.oingmaryho.business.orderservice.application.service.feignclient.ProductClient;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderJPARepository;
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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final CacheManager cacheManager;
    private final CompanyClient companyClient;
    private final ProductClient productClient;
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;
    private final OrderJPARepository orderJPARepository;
    private final OrderApplicationMapper orderApplicationMapper;

    @Value("${message.queue.product}")
    private String queueProduct;

    @Value("${message.queue.err.product}")
    private String queueErrProduct;

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "orders")
    public Page<OrderResponseServiceDto> getOrders(Long userId, String username, String slackId, String role, OrdersRequestServiceDto ordersRequestServiceDto) {
        // TODO: Role - HubManager, HubDeliveryManager, CompanyDeliveryManager, CompanyManager 설정하기

        log.info("{}, {}, {}, {}", userId, username, slackId, role);

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

    @Transactional(readOnly = true)
    public OrderResponseServiceDto getOrder(Long userId, String username, String slackId, String role, OrderRequestServiceDto orderRequestServiceDto) {
        // TODO: Role 검사하기

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
    public void createOrder(OrderCreateRequestServiceDto create) {
        int totalPrice = 0;
        ArrayList<OrderDetail> details = new ArrayList<>();

        log.info("-");
        CompanyDetailsSearchResponseDto requestCompanyInfo = getCompanyInfo(create.requesterId());
        log.info("requestCompanyInfo: {}", requestCompanyInfo);

        Order order = Order.builder()
            .requesterId(requestCompanyInfo.id())
            .requesterSlackId(create.slackId())
            .requesterName(requestCompanyInfo.name())
            .requesterAddress(requestCompanyInfo.address())
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
            log.info("성공");

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
    public void updateOrder(Long userId, String username, String slackId, OrderUpdateServiceDto update) {
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
        log.info("주문 수정 완료");

        refreshCache(order);
    }

    @Transactional
    public void deleteOrder(Long userId, String username, String slackId, OrderDeleteServiceDto delete) {
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
    public void deleteOrderDetail(Long userId, String username, String slackId, OrderDetailDeleteRequestServiceDto request) {
        UUID orderId = request.orderId();

        Order order = getByOrderId(orderId);
        OrderDetail orderDetail = getByOrderDetailId(order, request.orderDetailId());

        orderDetail.delete();
        log.info("주문: {}, 상세 주문: {}, 삭제 완료", order.getId(), orderDetail.getId());

        Integer orderDetailPrice = (orderDetail.getQuantity() * orderDetail.getPrice());

        order.updateTotalPrice(orderDetailPrice);

        evictCache(order);
    }

    private Order getByOrderId(UUID orderId) {
        return orderJPARepository.findById(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));
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
        return productClient.getProduct(productId)
            .orElseThrow(() -> new OrderException(ErrorCode.PRODUCT_NOT_FOUND));
    }
}