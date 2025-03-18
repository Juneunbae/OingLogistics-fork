package com.oingmaryho.business.orderservice.presentation.controller;

import com.oingmaryho.business.orderservice.application.dto.request.OrdersRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.service.OrderService;
import com.oingmaryho.business.orderservice.presentation.dto.mapper.OrderPresentationMapper;
import com.oingmaryho.business.orderservice.utils.PageableUtils;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}