package com.oingmaryho.business.delivery_service.presentation.controller;

import com.oingmaryho.business.delivery_service.application.service.DeliveryService;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;
import com.oingmaryho.business.delivery_service.domain.type.UserRoleType;
import com.oingmaryho.business.delivery_service.presentation.dto.request.*;
import com.oingmaryho.business.delivery_service.utils.PageableUtils;
import com.oingmaryho.business.delivery_service.presentation.dto.mapper.DeliveryPresentationMapper;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.DeliveryRouteResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;
    private final DeliveryPresentationMapper deliveryPresentationMapper;


    @PutMapping("/{id}")
    public ResponseEntity<DeliveryUpdateResponseDto> updateDelivery(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateRequestDto requestDto) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliveryUpdateRequestServiceDto requestServiceDto = deliveryPresentationMapper.toUpdateServiceDto(id, requestDto);
        DeliveryUpdateResponseServiceDto responseServiceDto = deliveryService.updateDelivery(
                userId,
                UserRoleType.HUB_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(deliveryPresentationMapper.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryUpdateStatusResponseDto> updateDeliveryStatus(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusRequestDto requestDto) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliveryUpdateStatusRequestServiceDto requestServiceDto = deliveryPresentationMapper.toUpdateStatusServiceDto(id, requestDto);
        DeliveryUpdateStatusResponseServiceDto responseServiceDto = deliveryService.updateStatusDelivery(
                userId,
                UserRoleType.HUB_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(deliveryPresentationMapper.toUpdateStatusResponseDto(responseServiceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(
            HttpServletRequest request,
            @PathVariable UUID id) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliveryDeletionRequestServiceDto requestServiceDto = deliveryPresentationMapper.toDeletionServiceDto(id);
        deliveryService.deleteDelivery(
                userId,
                UserRoleType.HUB_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.noContent().build();
    }

    // 배송 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> getDeliveryDetail(
            HttpServletRequest request,
            @PathVariable UUID id) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliveryDetailRequestServiceDto requestServiceDto = deliveryPresentationMapper.toDetailServiceDto(id);
        DeliveryResponseServiceDto responseServiceDto = deliveryService.GetDeliveryDetail(
                userId,
                UserRoleType.HUB_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(deliveryPresentationMapper.toDetailResponseDto(responseServiceDto));
    }

    // 배송 전체 조회 (검색)
    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDto>> searchDelivery(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestParam(value = "id", required = false) UUID id,
            @RequestParam(value = "orderId", required = false) UUID orderId,
            @RequestParam(value = "orderDetailId", required = false) UUID orderDetailId,
            @RequestParam(value = "hubId", required = false) UUID hubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "status", required = false) DeliveryStatus status,
            @RequestParam(value = "managerId", required = false) Long managerId) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliverySearchRequestDto requestDto = new DeliverySearchRequestDto(
                id,
                orderId,
                orderDetailId,
                hubId,
                companyId,
                status,
                managerId,
                Boolean.FALSE);

        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);

        DeliverySearchRequestServiceDto requestServiceDto = deliveryPresentationMapper.toSearchServiceDto(requestDto, customPageable);
        Page<DeliveryResponseServiceDto> responseServiceDtos = deliveryService.GetDeliveriesBySearch(
                1L,
                UserRoleType.HUB_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(deliveryPresentationMapper::toSearchResponseDto));
    }

    // 배송 경로 조회
    @GetMapping("/routes/{id}")
    public ResponseEntity<DeliveryRouteResponseDto> getDeliveryRouteDetail(
            HttpServletRequest request,
            @PathVariable UUID id) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliveryRouteDetailRequestServiceDto requestServiceDto = deliveryPresentationMapper.toRouteDetailServiceDto(id);
        DeliveryRouteResponseServiceDto responseServiceDto = deliveryService.GetDeliveryRouteDetail(
                userId,
                UserRoleType.HUB_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(deliveryPresentationMapper.toRouteDetailResponseDto(responseServiceDto));
    }

    // 배송 경로 전체 조회 (검색)
    @GetMapping("/{id}/routes")
    public ResponseEntity<Page<DeliveryRouteResponseDto>> searchDeliveryRoute(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestParam(value = "routeId", required = false) UUID routeId,
            @RequestParam(value = "orderId", required = false) UUID orderId,
            @RequestParam(value = "orderDetailId", required = false) UUID orderDetailId,
            @RequestParam(value = "departureHubId", required = false) UUID departureHubId,
            @RequestParam(value = "arriveHubId", required = false) UUID arriveHubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "managerId", required = false) Long managerId,
            @RequestParam(value = "status", required = false) DeliveryRouteStatus status) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliveryRouteSearchRequestDto requestDto = new DeliveryRouteSearchRequestDto(
                routeId,
                orderId,
                orderDetailId,
                departureHubId,
                arriveHubId,
                companyId,
                managerId,
                status,
                Boolean.FALSE);

        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);
        DeliveryRouteSearchRequestServiceDto requestServiceDto = deliveryPresentationMapper.toRouteSearchServiceDto(id, requestDto, customPageable);
        Page<DeliveryRouteResponseServiceDto> responseServiceDtos = deliveryService.GetDeliveryRoutesBySearch(
                userId,
                UserRoleType.HUB_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(deliveryPresentationMapper::toRouteSearchResponseDto));
    }

    @PutMapping("/routes/{id}/status")
    public ResponseEntity<DeliveryRouteUpdateStatusResponseDto> updateDeliveryRouteStatus(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryRouteUpdateStatusRequestDto requestDto) {

        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        // 권한 체크

        DeliveryRouteUpdateStatusRequestServiceDto requestServiceDto = deliveryPresentationMapper.toUpdateRouteStatusServiceDto(id, requestDto);
        DeliveryRouteUpdateStatusResponseServiceDto responseServiceDto =
                deliveryService.updateRouteStatusDelivery(
                        userId,
                        UserRoleType.HUB_DELIVERY_MANAGER,
                        requestServiceDto);

        return ResponseEntity.ok(deliveryPresentationMapper.toUpdateRouteStatusResponseDto(responseServiceDto));
    }
}
