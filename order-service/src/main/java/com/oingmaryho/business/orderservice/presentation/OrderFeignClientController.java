package com.oingmaryho.business.orderservice.presentation;

import com.oingmaryho.business.orderservice.application.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.OrderFeignService;
import com.oingmaryho.business.orderservice.application.dto.OrderServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
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
    private final OrderApplicationMapper orderApplicationMapper;

    @Description(
        "FeignClient - 주문 조회"
    )
    @GetMapping("/{id}")
    public ResponseEntity<Order> orderServiceGetById(@PathVariable UUID id) {
        OrderServiceDto orderServiceDto = orderApplicationMapper.toOrderServiceDto(id);
        return ResponseEntity.ok(orderFeignService.orderServiceGetById(orderServiceDto));
    }
}