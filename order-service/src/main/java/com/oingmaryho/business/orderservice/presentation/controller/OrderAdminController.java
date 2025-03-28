package com.oingmaryho.business.orderservice.presentation.controller;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.common.infrastructure.annotation.RequiredRoles;
import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderCreateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.service.OrderAdminService;
import com.oingmaryho.business.orderservice.presentation.dto.mapper.OrderPresentationMapper;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderAdminRequestServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderCreateRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderUpdateRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderAdminResponseServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderCreateResponseDto;
import com.oingmaryho.business.orderservice.utils.PageableUtils;
import jakarta.servlet.http.HttpServletRequest;
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
@RequestMapping("/admin/v1/orders")
@RequiredArgsConstructor
public class OrderAdminController {
    private final OrderAdminService orderAdminService;
    private final OrderPresentationMapper orderPresentationMapper;

    @Description(
        "마스터 - 주문 전체 조회"
    )
    @GetMapping
    @RequiredRoles({UserRoleType.MASTER})
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

        OrderAdminRequestServiceDto orderAdminRequestServiceDto = orderPresentationMapper.toOrderAdminRequestServiceDto(
            productName,
            recipientName,
            requesterName,
            isDeleted,
            customPageable
        );
        Page<OrderAdminResponseServiceDto> response = orderAdminService.getOrders(orderAdminRequestServiceDto);

        return ResponseEntity.ok(response.map(orderPresentationMapper::toOrderAdminResponseServiceDto));
    }

    @Description(
        "마스터 - 주문 생성"
    )
    @PostMapping
    @RequiredRoles({UserRoleType.MASTER})
    public ResponseEntity<OrderCreateResponseDto> createOrder(HttpServletRequest request, @RequestBody OrderCreateRequestDto orderCreateRequestDto) {
        Long userId = (Long) request.getAttribute("userId");
        String username = (String) request.getAttribute("username");
        String slackId = (String) request.getAttribute("slackId");

        List<OrderDetailCreateRequestServiceDto> orderCreateRequestServiceDto = orderCreateRequestDto.orderDetailCreateDto().stream().map(
            orderPresentationMapper::toOrderDetailCreateRequestServiceDto
        ).toList();

        OrderCreateRequestServiceDto create = orderPresentationMapper.toOrderCreateRequestServiceDto(
            userId, username, slackId, orderCreateRequestDto, orderCreateRequestServiceDto
        );

        OrderCreateResponseServiceDto orderCreateResponseServiceDto = orderAdminService.createOrder(create);

        return ResponseEntity.ok(orderPresentationMapper.toOrderCreateResponseDto(orderCreateResponseServiceDto));
    }

    @Description(
        "마스터 - 주문 상세 조회"
    )
    @GetMapping("/{id}")
    @RequiredRoles({UserRoleType.MASTER})
    public ResponseEntity<?> getOrder(@PathVariable UUID id) {
        OrderRequestServiceDto orderRequestServiceDto = orderPresentationMapper.toOrderServiceDto(id);
        OrderResponseServiceDto response = orderAdminService.getOrder(orderRequestServiceDto);

        return ResponseEntity.ok(orderPresentationMapper.toOrderResponseServiceDto(response));
    }

    @Description(
        "마스터 - 주문 수정하기"
    )
    @PutMapping("/{id}")
    @RequiredRoles({UserRoleType.MASTER})
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
    @RequiredRoles({UserRoleType.MASTER})
    public ResponseEntity<Void> deleteOrder(HttpServletRequest request, @PathVariable UUID id) {
        Long userId = (Long) request.getAttribute("userId");

        OrderDeleteServiceDto orderDeleteServiceDto = orderPresentationMapper.toOrderDeleteDto(id);
        orderAdminService.deleteOrder(userId, orderDeleteServiceDto);

        return ResponseEntity.ok().build();
    }

    @Description(
        "마스터 - 상세 주문 삭제"
    )
    @RequiredRoles({UserRoleType.MASTER})
    @DeleteMapping("/{id}/details/{orderDetailId}")
    public ResponseEntity<Void> deleteOrderDetail(HttpServletRequest request, @PathVariable UUID id, @PathVariable UUID orderDetailId) {
        Long userId = (Long) request.getAttribute("userId");

        OrderDetailDeleteRequestServiceDto orderDetailDeleteRequestServiceDto = orderPresentationMapper.toOrderDetailDeleteRequestServiceDto(id, orderDetailId);
        orderAdminService.deleteOrderDetail(userId, orderDetailDeleteRequestServiceDto);

        return ResponseEntity.ok().build();
    }
}