package com.oingmaryho.business.orderservice.presentation.controller;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.common.infrastructure.annotation.RequiredRoles;
import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.service.OrderService;
import com.oingmaryho.business.orderservice.presentation.dto.mapper.OrderPresentationMapper;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderCreateRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderUpdateRequestDto;
import com.oingmaryho.business.orderservice.utils.PageableUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Description;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    @RequiredRoles({UserRoleType.HUB_DELIVERY_MANAGER, UserRoleType.COMPANY_DELIVERY_MANAGER, UserRoleType.COMPANY_MANAGER, UserRoleType.HUB_MANAGER})
    public ResponseEntity<?> getOrders(
        HttpServletRequest request,
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
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);

        OrdersRequestServiceDto ordersRequestServiceDto = orderPresentationMapper.toOrdersServiceDto(
            productName, recipientName, requesterName, isDeleted, customPageable
        );
        Page<OrderResponseServiceDto> response = orderService.getOrders(userId, role, ordersRequestServiceDto);

        return ResponseEntity.ok(response.map(orderPresentationMapper::toOrderResponseServiceDto));
    }

    @Description(
        "허브 관리자, 허브 배송 담당자, 업체 배송 담당자, 업체 담당자 - 주문 상세 조회"
    )
    @GetMapping("/{id}")
    @RequiredRoles({UserRoleType.HUB_MANAGER, UserRoleType.HUB_DELIVERY_MANAGER, UserRoleType.COMPANY_DELIVERY_MANAGER, UserRoleType.COMPANY_MANAGER})
    public ResponseEntity<?> getOrder(HttpServletRequest request, @PathVariable UUID id) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        OrderRequestServiceDto orderRequestServiceDto = orderPresentationMapper.toOrderServiceDto(id);
        OrderResponseServiceDto response = orderService.getOrder(userId, role, orderRequestServiceDto);

        return ResponseEntity.ok(orderPresentationMapper.toOrderResponseServiceDto(response));
    }

    @Description(
        "허브 관리자, 허브 배송 담당자, 업체 배송 담당자, 업체 담당자 - 주문 생성"
    )
    @PostMapping
    @RequiredRoles({UserRoleType.HUB_MANAGER, UserRoleType.HUB_DELIVERY_MANAGER, UserRoleType.COMPANY_DELIVERY_MANAGER, UserRoleType.COMPANY_MANAGER})
    public ResponseEntity<?> createOrder(HttpServletRequest request, @Valid @RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        String slackId = (String) request.getAttribute("slackId");

        List<OrderDetailCreateRequestServiceDto> orderCreateRequestServiceDto = orderCreateRequestDto.orderDetailCreateDto().stream().map(
            orderPresentationMapper::toOrderDetailCreateRequestServiceDto
        ).toList();

        OrderCreateRequestServiceDto create = orderPresentationMapper.toOrderCreateRequestServiceDto(
            userId, username, slackId, orderCreateRequestDto, orderCreateRequestServiceDto
        );

        orderService.createOrder(create);
        return ResponseEntity.ok().build();
    }

    @Description(
        "허브 관리자 - 주문 수정"
    )
    @PutMapping("/{id}")
    @RequiredRoles({UserRoleType.HUB_MANAGER})
    public ResponseEntity<Void> updateOrder(HttpServletRequest request, @PathVariable UUID id, @RequestBody OrderUpdateRequestDto update) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        OrderUpdateServiceDto orderUpdateServiceDto = orderPresentationMapper.toOrderUpdateServiceDto(
            id, update, update.requestOrderDetails().stream().map(
                orderPresentationMapper::toOrderDetailUpdateServiceDto
            ).toList()
        );
        orderService.updateOrder(userId, role, orderUpdateServiceDto);

        return ResponseEntity.ok().build();
    }

    @Description(
        "허브 관리자 - 주문 삭제"
    )
    @DeleteMapping("/{id}")
    @RequiredRoles({UserRoleType.HUB_MANAGER})
    public ResponseEntity<Void> deleteOrder(HttpServletRequest request, @PathVariable UUID id) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        OrderDeleteServiceDto orderDeleteServiceDto = orderPresentationMapper.toOrderDeleteDto(id);
        orderService.deleteOrder(userId, role, orderDeleteServiceDto);

        return ResponseEntity.ok().build();
    }

    @Description(
        "허브 관리자 - 상세 주문 삭제"
    )
    @RequiredRoles({UserRoleType.HUB_MANAGER})
    @DeleteMapping("/{id}/details/{orderDetailId}")
    public ResponseEntity<Void> deleteOrderDetail(HttpServletRequest request, @PathVariable UUID id, @PathVariable UUID orderDetailId) {
        Long userId = (Long) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");

        OrderDetailDeleteRequestServiceDto orderDetailDeleteRequestServiceDto = orderPresentationMapper.toOrderDetailDeleteRequestServiceDto(id, orderDetailId);
        orderService.deleteOrderDetail(userId, role, orderDetailDeleteRequestServiceDto);

        return ResponseEntity.ok().build();
    }
}