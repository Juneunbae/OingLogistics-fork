package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.service.DeliveryAdminService;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.UserRoleType;
import com.oingmaryho.business.delivery_service.utils.PageableUtils;
import com.oingmaryho.business.delivery_service.presentation.dto.mapper.DeliveryPresentationMapper;
import com.oingmaryho.business.delivery_service.presentation.dto.request.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
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

    @PostMapping
    public ResponseEntity<DeliveryCreationResponseDto> createDelivery(
            @RequestBody DeliveryCreationRequestDto requestDto) {
        // TODO change userId, userRole type from UserVO
        DeliveryCreationRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toCreationServiceDto(requestDto);
        DeliveryCreationResponseServiceDto responseServiceDto = deliveryAdminService.createDelivery(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toCreationResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryUpdateResponseDto> updateDelivery(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateRequestDto requestDto) {
        // TODO change userId, userRole type from UserVO
        DeliveryUpdateRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateServiceDto(id, requestDto);
        DeliveryUpdateResponseServiceDto responseServiceDto = deliveryAdminService.updateDelivery(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryUpdateStatusResponseDto> updateDeliveryStatus(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusRequestDto requestDto) {
        // TODO change userId, userRole type from UserVO
        DeliveryUpdateStatusRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateStatusServiceDto(id, requestDto);
        DeliveryUpdateStatusResponseServiceDto responseServiceDto = deliveryAdminService.updateStatusDelivery(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateStatusResponseDto(responseServiceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(
            @PathVariable UUID id) {
        // TODO change userId, userRole type from UserVO
        DeliveryDeletionRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDeletionServiceDto(id);
        deliveryAdminService.deleteDelivery(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);
        return ResponseEntity.noContent().build();
    }

    // 배송 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> getDeliveryDetail(
            @PathVariable UUID id) {
        // TODO change userId, userRole type from UserVO
        DeliveryDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDetailServiceDto(id);
        DeliveryResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveryDetail(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toDetailResponseDto(responseServiceDto));
    }

    // 배송 전체 조회 (검색)
    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDto>> searchDelivery(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestParam(value = "hubId", required = false) UUID hubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "managerId", required = false) UUID managerId) {

        DeliverySearchRequestDto requestDto = new DeliverySearchRequestDto(hubId, companyId, managerId);
        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);
        // TODO change userId, userRole type from UserVO
        DeliverySearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toSearchServiceDto(requestDto, customPageable);
        Page<DeliveryResponseServiceDto> responseServiceDtos = deliveryAdminService.GetDeliveriesBySearch(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(DeliveryPresentationMapper.INSTANCE::toSearchResponseDto));
    }

    // 배송 경로 조회
    @GetMapping("/routes/{id}")
    public ResponseEntity<DeliveryRouteResponseDto> getDeliveryRouteDetail(
            @PathVariable UUID id) {
        // TODO change userId, userRole type from UserVO
        DeliveryRouteDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteDetailServiceDto(id);
        DeliveryRouteResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveryRouteDetail(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toRouteDetailResponseDto(responseServiceDto));
    }

    // 배송 경로 전체 조회 (검색)
    @GetMapping("/{id}/routes")
    public ResponseEntity<Page<DeliveryRouteResponseDto>> searchDeliveryRoute(
            @PathVariable UUID id,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestParam(value = "hubId", required = false) UUID hubId,
            @RequestParam(value = "companyId", required = false) UUID companyId,
            @RequestParam(value = "managerId", required = false) UUID managerId) {

        DeliveryRouteSearchRequestDto requestDto = new DeliveryRouteSearchRequestDto(hubId, companyId, managerId);
        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);
        // TODO change userId, userRole type from UserVO
        DeliveryRouteSearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteSearchServiceDto(id, requestDto, customPageable);
        Page<DeliveryRouteResponseServiceDto> responseServiceDtos = deliveryAdminService.GetDeliveryRoutesBySearch(1L, UserRoleType.HUB_DELIVERY_MANAGER,requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(DeliveryPresentationMapper.INSTANCE::toRouteSearchResponseDto));
    }

    @PutMapping("/routes/{id}/status")
    public ResponseEntity<DeliveryRouteUpdateStatusResponseDto> updateDeliveryRouteStatus(
            @PathVariable UUID id,
            @RequestBody DeliveryRouteUpdateStatusRequestDto requestDto) {

        // TODO change userId, userRole type from UserVO
        DeliveryRouteUpdateStatusRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateRouteStatusServiceDto(id, requestDto);
        DeliveryRouteUpdateStatusResponseServiceDto responseServiceDto = deliveryAdminService.updateRouteStatusDelivery(1L, UserRoleType.HUB_DELIVERY_MANAGER, requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateRouteStatusResponseDto(responseServiceDto));
    }



}
