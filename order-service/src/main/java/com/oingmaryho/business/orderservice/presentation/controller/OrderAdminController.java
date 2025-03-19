package com.oingmaryho.business.orderservice.presentation.controller;

import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.service.OrderAdminService;
import com.oingmaryho.business.orderservice.config.pageable.PageableConfig;
import com.oingmaryho.business.orderservice.presentation.dto.mapper.OrderPresentationMapper;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderUpdateRequestDto;
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
    private final PageableConfig pageableConfig;
    private final OrderAdminService orderAdminService;
    private final OrderPresentationMapper orderPresentationMapper;

    @Description(
        "마스터 - 주문 전체 조회"
    )
    @GetMapping
    public ResponseEntity<?> getOrders(
        @Min(value = 1, message = "페이지 번호는 1 이상이어야 합니다.")
        @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
        @RequestParam(value = "size", required = false) Integer size,
        @RequestParam(value = "sortDirection", required = false) String sortDirection,
        @RequestParam(value = "by", required = false) String by,
        @RequestParam(value = "productName", required = false) String productName,
        @RequestParam(value = "recipientName", required = false) String recipientName,
        @RequestParam(value = "requesterName", required = false) String requesterName,
        @RequestParam(value = "isDeleted", required = false) Boolean isDeleted
    ) {
        Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
        OrdersRequestServiceDto ordersRequestServiceDto = orderPresentationMapper.toOrdersServiceDto(
            productName,
            recipientName,
            requesterName,
            isDeleted,
            customPageable
        );
        Page<OrderResponseServiceDto> response = orderAdminService.getOrders(ordersRequestServiceDto);

        return ResponseEntity.ok(response.map(orderPresentationMapper::toOrderResponseServiceDto));
    }

    @Description(
        "마스터 - 주문 상세 조회"
    )
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrder(@PathVariable UUID id) {
        OrderRequestServiceDto orderRequestServiceDto = orderPresentationMapper.toOrderServiceDto(id);
        OrderResponseServiceDto response = orderAdminService.getOrder(orderRequestServiceDto);

        return ResponseEntity.ok(orderPresentationMapper.toOrderResponseServiceDto(response));
    }

    @Description(
        "마스터,- 주문 수정하기"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrder(@PathVariable UUID id, @RequestBody OrderUpdateRequestDto update) {
        OrderUpdateServiceDto orderUpdateServiceDto = orderPresentationMapper.toOrderUpdateServiceDto(
            id, update, update.requestOrderDetails().stream().map(
                orderPresentationMapper::toOrderDetailUpdateServiceDto
            ).toList()
        );
        orderAdminService.updateOrder(orderUpdateServiceDto);

        return ResponseEntity.ok().build();
    }

    @Description(
        "마스터 - 주문 삭제"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        OrderDeleteServiceDto orderDeleteServiceDto = orderPresentationMapper.toOrderDeleteDto(id);
        orderAdminService.deleteOrder(orderDeleteServiceDto);

        return ResponseEntity.ok().build();
    }

    @Description(
        "마스터 - 상세 주문 삭제"
    )
    @DeleteMapping("/{id}/details/{orderDetailId}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable UUID id, @PathVariable UUID orderDetailId) {
        OrderDetailDeleteRequestServiceDto request = orderPresentationMapper.toOrderDetailDeleteRequestServiceDto(id, orderDetailId);
        orderAdminService.deleteOrderDetail(request);

        return ResponseEntity.ok().build();
    }
}