package com.oingmaryho.business.orderservice.application.service;

import com.oingmaryho.business.orderservice.application.dto.request.OrderRequestServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderFeignService {
    private final OrderJPARepository orderJPARepository;

    @Transactional
    public Order orderServiceGetById(OrderRequestServiceDto orderRequestServiceDto) {
        return getByOrderId(orderRequestServiceDto.orderId());
    }

    public Order getByOrderId(UUID orderId) {
        return orderJPARepository.findById(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));
    }
}