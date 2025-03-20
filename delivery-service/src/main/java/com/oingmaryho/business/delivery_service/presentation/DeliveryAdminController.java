package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.service.DeliveryAdminService;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.type.UserRoleType;
import com.oingmaryho.business.delivery_service.utils.PageableUtils;
import com.oingmaryho.business.delivery_service.presentation.dto.mapper.DeliveryPresentationMapper;
import com.oingmaryho.business.delivery_service.presentation.dto.request.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/v1/deliveries")
public class DeliveryAdminController {

    private final DeliveryAdminService deliveryAdminService;
    private final DeliveryPresentationMapper deliveryPresentationMapper;

    @PostMapping
    public ResponseEntity<DeliveryCreationResponseDto> createDelivery(
            HttpServletRequest request,
            @RequestBody DeliveryCreationRequestDto requestDto) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryCreationRequestServiceDto requestServiceDto = deliveryPresentationMapper.toCreationServiceDto(requestDto);
        DeliveryCreationResponseServiceDto responseServiceDto = deliveryAdminService.createDelivery(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);
        return ResponseEntity.ok(deliveryPresentationMapper.toCreationResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryUpdateResponseDto> updateDelivery(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateRequestDto requestDto) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryUpdateRequestServiceDto requestServiceDto = deliveryPresentationMapper.toUpdateServiceDto(id, requestDto);
        DeliveryUpdateResponseServiceDto responseServiceDto = deliveryAdminService.updateDelivery(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);
        return ResponseEntity.ok(deliveryPresentationMapper.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryUpdateStatusResponseDto> updateDeliveryStatus(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusRequestDto requestDto) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryUpdateStatusRequestServiceDto requestServiceDto = deliveryPresentationMapper.toUpdateStatusServiceDto(id, requestDto);
        DeliveryUpdateStatusResponseServiceDto responseServiceDto = deliveryAdminService.updateStatusDelivery(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);
        return ResponseEntity.ok(deliveryPresentationMapper.toUpdateStatusResponseDto(responseServiceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(
            HttpServletRequest request,
            @PathVariable UUID id) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryDeletionRequestServiceDto requestServiceDto = deliveryPresentationMapper.toDeletionServiceDto(id);
        deliveryAdminService.deleteDelivery(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);
        return ResponseEntity.noContent().build();
    }

    // 배송 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryAdminResponseDto> getDeliveryDetail(
            HttpServletRequest request,
            @PathVariable UUID id) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryDetailRequestServiceDto requestServiceDto = deliveryPresentationMapper.toDetailServiceDto(id);
        DeliveryResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveryDetail(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);
        return ResponseEntity.ok(deliveryPresentationMapper.toDetailAdminResponseDto(responseServiceDto));
    }

    // 배송 전체 조회 (검색)
    @GetMapping
    public ResponseEntity<Page<DeliveryAdminResponseDto>> searchDelivery(
            HttpServletRequest request,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestParam(value = "hubId", required = false) UUID hubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "managerId", required = false) UUID managerId,
            @RequestParam(value = "isDeleted", required = false) Boolean isDeleted) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliverySearchRequestDto requestDto = new DeliverySearchRequestDto(hubId, companyId, managerId, isDeleted);
        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);

        DeliverySearchRequestServiceDto requestServiceDto = deliveryPresentationMapper.toSearchServiceDto(requestDto, customPageable);
        Page<DeliveryResponseServiceDto> responseServiceDtos = deliveryAdminService.GetDeliveriesBySearch(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(deliveryPresentationMapper::toSearchAdminResponseDto));
    }

    // 배송 경로 조회
    @GetMapping("/routes/{id}")
    public ResponseEntity<DeliveryRouteAdminResponseDto> getDeliveryRouteDetail(
            HttpServletRequest request,
            @PathVariable UUID id) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryRouteDetailRequestServiceDto requestServiceDto = deliveryPresentationMapper.toRouteDetailServiceDto(id);
        DeliveryRouteResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveryRouteDetail(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);
        return ResponseEntity.ok(deliveryPresentationMapper.toRouteDetailAdminResponseDto(responseServiceDto));
    }

    // 배송 경로 전체 조회 (검색)
    @GetMapping("/{id}/routes")
    public ResponseEntity<Page<DeliveryRouteAdminResponseDto>> searchDeliveryRoute(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false, defaultValue = "10") Integer size,
            @RequestParam(value = "sortDirection", required = false, defaultValue = "DESC") String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestParam(value = "hubId", required = false) UUID hubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "managerId", required = false) UUID managerId,
            @RequestParam(value = "isDeleted", required = false) Boolean isDeleted) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryRouteSearchRequestDto requestDto = new DeliveryRouteSearchRequestDto(hubId, companyId, managerId, isDeleted);
        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);

        DeliveryRouteSearchRequestServiceDto requestServiceDto = deliveryPresentationMapper.toRouteSearchServiceDto(id, requestDto, customPageable);
        Page<DeliveryRouteResponseServiceDto> responseServiceDtos = deliveryAdminService.GetDeliveryRoutesBySearch(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(deliveryPresentationMapper::toRouteSearchAdminResponseDto));
    }

    @PutMapping("/routes/{id}/status")
    public ResponseEntity<DeliveryRouteUpdateStatusResponseDto> updateDeliveryRouteStatus(
            HttpServletRequest request,
            @PathVariable UUID id,
            @RequestBody DeliveryRouteUpdateStatusRequestDto requestDto) {

        // 인터셉터에서 처리한 userId, userRole을 가져온다.
        Long userId = (Long) request.getAttribute("userId");
        UserRoleType userRole = (UserRoleType) request.getAttribute("userRole");

        DeliveryRouteUpdateStatusRequestServiceDto requestServiceDto = deliveryPresentationMapper.toUpdateRouteStatusServiceDto(id, requestDto);
        DeliveryRouteUpdateStatusResponseServiceDto responseServiceDto = deliveryAdminService.updateRouteStatusDelivery(
                userId,
                UserRoleType.COMPANY_DELIVERY_MANAGER,
                requestServiceDto);
        return ResponseEntity.ok(deliveryPresentationMapper.toUpdateRouteStatusResponseDto(responseServiceDto));
    }



}
