package com.oingmaryho.business.orderservice.application.service;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.orderservice.application.OrderHelper;
import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.HubSearchResponseDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderCreateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailUpdateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.event.OrderEvent;
import com.oingmaryho.business.orderservice.application.service.feignclient.HubClient;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.domain.OrderSearchCriteria;
import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderQueryRepository;
import com.oingmaryho.business.orderservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {
    private final HubClient hubClient;
    private final OrderHelper orderHelper;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderApplicationMapper orderApplicationMapper;

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
        Order order = orderHelper.getByOrderId(orderId);

        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            HubSearchResponseDto hubInfo = hubClient.getHubById(userId);

            UUID hubId = hubInfo.id();
            if (!order.getRequesterId().equals(hubId)) {
                orderHelper.evictCache(order);
                throw new OrderException(ErrorCode.HUB_NOT_MATCH);
            }
        }

        if (
            Objects.equals(role, UserRoleType.COMPANY_MANAGER.name())
                || Objects.equals(role, UserRoleType.HUB_DELIVERY_MANAGER.name())
                || Objects.equals(role, UserRoleType.COMPANY_DELIVERY_MANAGER.name())
        ) {
            if (!order.getRequesterUserId().equals(userId)) {
                orderHelper.evictCache(order);
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
    public OrderCreateResponseServiceDto createOrder(OrderCreateRequestServiceDto create) {
        int totalPrice = 0;

        CompanyDetailsSearchResponseDto requestCompanyInfo = orderHelper.getCompanyInfo(create.requesterId());

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

        Order processOrder = orderHelper.createOrderDetails(create, order, totalPrice);

        orderRepository.save(processOrder);
        publisher.publishEvent(new OrderEvent(order));

        return orderApplicationMapper.toOrderCreateResponseDto(
            order,
            order.getOrderDetails().stream().map(
                orderApplicationMapper::toOrderDetailCreateResponseDto
            ).toList()
        );
    }

    @Transactional
    public void updateOrder(Long userId, String role, OrderUpdateServiceDto update) {
        int totalPrice = 0;
        UUID orderId = update.id();

        Order order = orderHelper.getByOrderId(orderId);

        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            HubSearchResponseDto hubInfo = hubClient.getHubById(userId);

            UUID hubId = hubInfo.id();
            if (!order.getRequesterId().equals(hubId)) {
                throw new OrderException(ErrorCode.HUB_NOT_MATCH);
            }
        }

        if (update.orderDetails() != null) {
            for (OrderDetailUpdateResponseServiceDto orderDetailDto : update.orderDetails()) {
                OrderDetail orderDetail = orderHelper.getByOrderDetailId(order, orderDetailDto.orderDetailId());
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
        log.info("주문: {}, 수정 완료", order.getId());

        orderHelper.refreshCache(order);
    }

    @Transactional
    public void deleteOrder(Long userId, String role, OrderDeleteServiceDto delete) {
        UUID orderId = delete.orderId();

        Order order = orderHelper.getByOrderId(orderId);

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

        orderHelper.evictCache(order);
    }

    @Transactional
    public void deleteOrderDetail(Long userId, String role, OrderDetailDeleteRequestServiceDto request) {
        UUID orderId = request.orderId();

        Order order = orderHelper.getByOrderId(orderId);

        if (Objects.equals(role, UserRoleType.HUB_MANAGER.name())) {
            HubSearchResponseDto hubInfo = hubClient.getHubById(userId);

            UUID hubId = hubInfo.id();
            if (!order.getRequesterId().equals(hubId)) {
                throw new OrderException(ErrorCode.HUB_NOT_MATCH);
            }
        }

        OrderDetail orderDetail = orderHelper.getByOrderDetailId(order, request.orderDetailId());

        orderDetail.softDeleted(userId);
        log.info("주문: {}, 상세 주문: {}, 삭제 완료", order.getId(), orderDetail.getId());

        Integer orderDetailPrice = (orderDetail.getQuantity() * orderDetail.getPrice());

        order.updateTotalPrice(orderDetailPrice);

        orderHelper.evictCache(order);
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

            HubSearchResponseDto hubInfo = orderHelper.getHubInfo(userId);
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
            return orderQueryRepository.findDynamicQueryForOther(
                createOrderSearchCriteria(ordersRequestServiceDto),
                customPageable,
                userId
            );
        }

        return new PageImpl<>(Collections.emptyList(), customPageable, 0);
    }
}