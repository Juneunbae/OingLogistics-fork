package com.oingmaryho.business.orderservice.presentation.controller;

import com.oingmaryho.business.orderservice.application.dto.request.OrderRequestServiceDto;
import com.oingmaryho.business.orderservice.application.service.OrderFeignService;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.presentation.dto.mapper.OrderPresentationMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/order-service/orders")
public class OrderFeignClientController {
    private final OrderFeignService orderFeignService;
    private final OrderPresentationMapper orderPresentationMapper;

    @Description(
        "FeignClient - 주문 조회"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Order> orderServiceGetById(@PathVariable UUID id) {
        OrderRequestServiceDto orderRequestServiceDto = orderPresentationMapper.toOrderServiceDto(id);
        return ResponseEntity.ok(orderFeignService.orderServiceGetById(orderRequestServiceDto));
    }
}