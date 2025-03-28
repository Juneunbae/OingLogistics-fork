package com.oingmaryho.business.orderservice.application.service;

import com.oingmaryho.business.orderservice.application.OrderHelper;
import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderCreateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailUpdateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.event.OrderEvent;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.domain.OrderSearchCriteria;
import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import com.oingmaryho.business.orderservice.infrastructure.OrderQueryRepository;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderAdminRequestServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderAdminResponseServiceDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderAdminService {
    private final OrderHelper orderHelper;
    private final CacheManager cacheManager;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher publisher;
    private final OrderQueryRepository orderQueryRepository;
    private final OrderApplicationMapper orderApplicationMapper;

    @Transactional
    public Page<OrderAdminResponseServiceDto> getOrders(OrderAdminRequestServiceDto orderAdminRequestServiceDto) {
        String cacheKey = makeOrdersCacheKey(orderAdminRequestServiceDto);

        Page<OrderAdminResponseServiceDto> cachedOrders = orderHelper.getOrdersCache(cacheKey);
        if (cachedOrders != null) {
            return cachedOrders;
        }

        log.info("OrderRequestDto : {}", orderAdminRequestServiceDto);

        Pageable customPageable = orderAdminRequestServiceDto.customPageable();

        Page<Order> orders = orderQueryRepository.findDynamicQuery(
            createOrderSearchCriteria(orderAdminRequestServiceDto),
            customPageable
        );

        List<OrderAdminResponseServiceDto> ordersDto = orders.stream().map(
            order -> orderApplicationMapper.toOrderAdminResponseDto(
                order,
                order.getOrderDetails().stream().map(
                    orderDetail -> orderApplicationMapper.toOrderDetailDto(order.getId(), orderDetail)
                ).toList()
            )
        ).toList();

        Page<OrderAdminResponseServiceDto> results = new PageImpl<>(ordersDto, customPageable, orders.getTotalElements());
        putOrdersCache(cacheKey, results);

        return results;
    }

    @Transactional
    public OrderResponseServiceDto getOrder(OrderRequestServiceDto orderRequestServiceDto) {
        UUID orderId = orderRequestServiceDto.orderId();
        Order order = orderHelper.getByOrderId(orderId);

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
    public void updateOrder(OrderUpdateServiceDto update) {
        int totalPrice = 0;
        UUID orderId = update.id();

        Order order = orderHelper.getByOrderId(orderId);

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
        log.info("주문:{}, 수정 완료", order.getId());

        orderHelper.refreshCache(order);
    }

    @Transactional
    public void deleteOrder(Long userId, OrderDeleteServiceDto delete) {
        UUID orderId = delete.orderId();

        Order order = orderHelper.getByOrderId(orderId);

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            orderDetail.softDeleted(userId);
            log.info("상세 주문: {},  삭제 완료", orderDetail.getId());
        }

        order.softDeleted(userId);
        log.info("주문: {}, 삭제 완료", order.getId());

        orderHelper.evictCache(order);
    }

    @Transactional
    public void deleteOrderDetail(Long userId, OrderDetailDeleteRequestServiceDto request) {
        UUID orderId = request.orderId();

        Order order = orderHelper.getByOrderId(orderId);
        OrderDetail orderDetail = orderHelper.getByOrderDetailId(order, request.orderDetailId());

        orderDetail.softDeleted(userId);
        log.info("주문: {}, 상세 주문: {}, 삭제 완료", order.getId(), orderDetail.getId());

        Integer orderDetailPrice = (orderDetail.getQuantity() * orderDetail.getPrice());

        order.updateTotalPrice(orderDetailPrice);

        orderHelper.evictCache(order);
    }

    private String makeOrdersCacheKey(OrderAdminRequestServiceDto orderAdminRequestServiceDto) {
        String productName = orderAdminRequestServiceDto.productName();
        productName = (productName == null) ? "" : productName;

        String recipientName = orderAdminRequestServiceDto.recipientName();
        recipientName = (recipientName == null) ? "" : recipientName;

        String requesterName = orderAdminRequestServiceDto.requesterName();
        requesterName = (requesterName == null) ? "" : requesterName;

        Boolean isDeleted = orderAdminRequestServiceDto.isDeleted();
        isDeleted = isDeleted != null && isDeleted;

        Pageable customPageable = orderAdminRequestServiceDto.customPageable();
        int customPageableHashCode = (customPageable != null) ? customPageable.hashCode() : 0;

        return "Orders_" +
            productName.hashCode() +
            recipientName.hashCode() +
            requesterName.hashCode() +
            isDeleted.hashCode() +
            customPageableHashCode;
    }

    private void putOrdersCache(String cacheKey, Page<OrderAdminResponseServiceDto> results) {
        Cache cache = cacheManager.getCache("orders");
        cache.put(cacheKey, results);
        log.info("캐시 저장 성공: {}", cacheKey);
    }

    private OrderSearchCriteria createOrderSearchCriteria(OrderAdminRequestServiceDto requestDto) {
        return OrderSearchCriteria.builder()
            .productName(requestDto.productName())
            .recipientName(requestDto.recipientName())
            .requesterName(requestDto.requesterName())
            .isDeleted(requestDto.isDeleted())
            .build();
    }
}