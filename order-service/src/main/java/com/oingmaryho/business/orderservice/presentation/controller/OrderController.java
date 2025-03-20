package com.oingmaryho.business.orderservice.presentation.controller;

import com.oingmaryho.business.orderservice.application.dto.request.OrderDeleteServiceDto;
import com.oingmaryho.business.orderservice.application.dto.request.OrderDetailDeleteRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.request.OrderUpdateServiceDto;
import com.oingmaryho.business.orderservice.application.dto.request.OrdersRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.service.OrderService;
import com.oingmaryho.business.orderservice.presentation.dto.mapper.OrderPresentationMapper;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderUpdateRequestDto;
import com.oingmaryho.business.orderservice.utils.PageableUtils;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final OrderPresentationMapper orderPresentationMapper;

    @Description(
        "허브 관리자, 허브 배송 담당자, 업체 배송 담당자, 업체 담당자 - 주문 전제 조회"
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
        @RequestParam(value = "isDeleted", required = false, defaultValue = "false") Boolean isDeleted
    ) {
        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);
        OrdersRequestServiceDto ordersRequestServiceDto = orderPresentationMapper.toOrdersServiceDto(
            productName, recipientName, requesterName, isDeleted, customPageable
        );
        Page<OrderResponseServiceDto> response = orderService.getOrders(ordersRequestServiceDto);

        return ResponseEntity.ok(response.map(orderPresentationMapper::toOrderResponseServiceDto));
    }

    @Description(
        "허브 관리자 - 주문 수정"
    )
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateOrder(@PathVariable UUID id, @RequestBody OrderUpdateRequestDto update) {
        OrderUpdateServiceDto orderUpdateServiceDto = orderPresentationMapper.toOrderUpdateServiceDto(
            id, update, update.requestOrderDetails().stream().map(
                orderPresentationMapper::toOrderDetailUpdateServiceDto
            ).toList()
        );
        orderService.updateOrder(orderUpdateServiceDto);

        return ResponseEntity.ok().build();
    }

    @Description(
        "허브 관리자 - 주문 삭제"
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable UUID id) {
        OrderDeleteServiceDto orderDeleteServiceDto = orderPresentationMapper.toOrderDeleteDto(id);
        orderService.deleteOrder(orderDeleteServiceDto);

        return ResponseEntity.ok().build();
    }

    @Description(
        "허브 관리자 - 상세 주문 삭제"
    )
    @DeleteMapping("/{id}/details/{orderDetailId}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable UUID id, @PathVariable UUID orderDetailId) {
        OrderDetailDeleteRequestServiceDto request = orderPresentationMapper.toOrderDetailDeleteRequestServiceDto(id, orderDetailId);
        orderService.deleteOrderDetail(request);

        return ResponseEntity.ok().build();
    }
}