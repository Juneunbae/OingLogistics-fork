package com.oingmaryho.business.orderservice.presentation;

import com.oingmaryho.business.orderservice.application.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.OrderService;
import com.oingmaryho.business.orderservice.application.dto.OrderServiceDto;
import com.oingmaryho.business.orderservice.application.dto.OrdersServiceDto;
import com.oingmaryho.business.orderservice.config.pageable.PageableConfig;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderSearchRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDto;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class OrderController {
    private final PageableConfig pageableConfig;
    private final OrderService orderService;
    private final OrderApplicationMapper orderApplicationMapper;

    @Description(
        "마스터 - 주문 전체 조회"
    )
    @GetMapping("/admin/v1/orders")
    public ResponseEntity<Page<OrderDto>> getOrders(
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "sortDirection", required = false) String sortDirection,
        @RequestBody OrderSearchRequestDto orderSearchRequestDto
    ) {
        Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
        OrdersServiceDto ordersServiceDto = orderApplicationMapper.toOrdersServiceDto(
            orderSearchRequestDto, customPageable
        );

        return ResponseEntity.ok(orderService.getOrders(ordersServiceDto));
    }

    @Description(
        "마스터 - 주문 상세 조회"
    )
    @GetMapping("/admin/v1/orders/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        OrderServiceDto orderServiceDto = orderApplicationMapper.toOrderServiceDto(id);

        return ResponseEntity.ok(orderService.getOrder(orderServiceDto));
    }

    @Description(
        "FeignClient - 주문 조회"
    )
    @GetMapping("/order-service/orders/{id}")
    public ResponseEntity<Order> getById(@PathVariable UUID id) {
        OrderServiceDto orderServiceDto = orderApplicationMapper.toOrderServiceDto(id);
        return ResponseEntity.ok(orderService.getById(orderServiceDto));
    }
}