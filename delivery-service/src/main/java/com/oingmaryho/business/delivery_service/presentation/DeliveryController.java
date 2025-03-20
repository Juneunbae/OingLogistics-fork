package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.service.DeliveryService;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
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

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryUpdateResponseDto> updateDelivery(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateRequestDto requestDto) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryUpdateRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateServiceDto(id, requestDto);
        DeliveryUpdateResponseServiceDto responseServiceDto = deliveryService.updateDelivery(
                userId,
                userRole,
                requestServiceDto);

        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryUpdateStatusResponseDto> updateDeliveryStatus(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusRequestDto requestDto) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryUpdateStatusRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateStatusServiceDto(id, requestDto);
        DeliveryUpdateStatusResponseServiceDto responseServiceDto = deliveryService.updateStatusDelivery(
                userId,
                userRole,
                requestServiceDto);

        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateStatusResponseDto(responseServiceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(
            HttpServletRequest request,
            @PathVariable UUID id) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryDeletionRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDeletionServiceDto(id);
        deliveryService.deleteDelivery(
                userId,
                userRole,
                requestServiceDto);

        return ResponseEntity.noContent().build();
    }

    // 배송 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> getDeliveryDetail(
            HttpServletRequest request,
            @PathVariable UUID id) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDetailServiceDto(id);
        DeliveryResponseServiceDto responseServiceDto = deliveryService.GetDeliveryDetail(
                userId,
                userRole,
                requestServiceDto);

        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toDetailResponseDto(responseServiceDto));
    }

    // 배송 전체 조회 (검색)
    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDto>> searchDelivery(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestParam(value = "hubId", required = false) UUID hubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "managerId", required = false) UUID managerId) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliverySearchRequestDto requestDto = new DeliverySearchRequestDto(hubId, companyId, managerId, Boolean.FALSE);
        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);

        DeliverySearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toSearchServiceDto(requestDto, customPageable);
        Page<DeliveryResponseServiceDto> responseServiceDtos = deliveryService.GetDeliveriesBySearch(
                userId,
                userRole,
                requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(DeliveryPresentationMapper.INSTANCE::toSearchResponseDto));
    }

    // 배송 경로 조회
    @GetMapping("/routes/{id}")
    public ResponseEntity<DeliveryRouteResponseDto> getDeliveryRouteDetail(
            HttpServletRequest request,
            @PathVariable UUID id) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryRouteDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteDetailServiceDto(id);
        DeliveryRouteResponseServiceDto responseServiceDto = deliveryService.GetDeliveryRouteDetail(
                userId,
                userRole,
                requestServiceDto);

        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toRouteDetailResponseDto(responseServiceDto));
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
            @RequestParam(value = "hubId", required = false) UUID hubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "managerId", required = false) UUID managerId) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryRouteSearchRequestDto requestDto = new DeliveryRouteSearchRequestDto(hubId, companyId, managerId, Boolean.FALSE);
        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);

        DeliveryRouteSearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteSearchServiceDto(id, requestDto, customPageable);
        Page<DeliveryRouteResponseServiceDto> responseServiceDtos = deliveryService.GetDeliveryRoutesBySearch(
                userId,
                userRole,
                requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(DeliveryPresentationMapper.INSTANCE::toRouteSearchResponseDto));
    }

    @PutMapping("/routes/{id}/status")
    public ResponseEntity<DeliveryRouteUpdateStatusResponseDto> updateDeliveryRouteStatus(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryRouteUpdateStatusRequestDto requestDto) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryRouteUpdateStatusRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateRouteStatusServiceDto(id, requestDto);
        DeliveryRouteUpdateStatusResponseServiceDto responseServiceDto =
                deliveryService.updateRouteStatusDelivery(
                        userId,
                        userRole,
                        requestServiceDto);

        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateRouteStatusResponseDto(responseServiceDto));
    }
}
