package com.oingmaryho.business.orderservice.presentation;

import com.oingmaryho.business.orderservice.application.OrderAdminService;
import com.oingmaryho.business.orderservice.application.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.OrderServiceDto;
import com.oingmaryho.business.orderservice.application.dto.OrderUpdateServiceDto;
import com.oingmaryho.business.orderservice.application.dto.OrdersServiceDto;
import com.oingmaryho.business.orderservice.config.pageable.PageableConfig;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderSearchRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderUpdateRequestDto;
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
@RequestMapping("/admin/v1/orders")
@RequiredArgsConstructor
public class OrderAdminController {
    private final OrderAdminService orderAdminService;
    private final PageableConfig pageableConfig;
    private final OrderApplicationMapper orderApplicationMapper;

    @Description(
        "마스터 - 주문 전체 조회"
    )
    @GetMapping
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

        return ResponseEntity.ok(orderAdminService.getOrders(ordersServiceDto));
    }

    @Description(
        "마스터 - 주문 상세 조회"
    )
    @GetMapping("/{id}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable UUID id) {
        OrderServiceDto orderServiceDto = orderApplicationMapper.toOrderServiceDto(id);

        return ResponseEntity.ok(orderAdminService.getOrder(orderServiceDto));
    }

    @Description(
        "마스터,- 주문 수정하기"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrder(@PathVariable UUID id, @RequestBody OrderUpdateRequestDto update) {
        OrderUpdateServiceDto orderUpdateServiceDto = orderApplicationMapper.toOrderUpdateServiceDto(
            id, update, update.requestOrderDetails().stream().map(
                orderApplicationMapper::toOrderDetailUpdateServiceDto
            ).toList()
        );

        orderAdminService.updateOrder(orderUpdateServiceDto);
        return ResponseEntity.ok().build();
    }
}