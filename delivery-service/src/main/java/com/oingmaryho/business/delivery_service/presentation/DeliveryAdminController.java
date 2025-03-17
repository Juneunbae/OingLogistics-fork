package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.DeliveryAdminService;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.config.pageable.PageableConfig;
import com.oingmaryho.business.delivery_service.presentation.dto.request.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeliveryAdminController {

    private final PageableConfig pageableConfig;
    private final DeliveryAdminService deliveryAdminService;

    @PostMapping("/admin/v1/deliveries")
    public ResponseEntity<DeliveryCreationResponseDto> createDelivery(
            @RequestBody DeliveryCreationRequestDto requestDto) {
        DeliveryCreationRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toCreationServiceDto(requestDto);
        DeliveryCreationResponseServiceDto responseServiceDto = deliveryAdminService.createDelivery(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toCreationResponseDto(responseServiceDto));
    }

    @PutMapping("/admin/v1/deliveries/{id}")
    public ResponseEntity<DeliveryUpdateResponseDto> createDelivery(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateRequestDto requestDto) {
        DeliveryUpdateRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateServiceDto(id, requestDto);
        DeliveryUpdateResponseServiceDto responseServiceDto = deliveryAdminService.updateDelivery(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/admin/v1/deliveries/{id}")
    public ResponseEntity<DeliveryUpdateResponseDto> updateDelivery(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateRequestDto requestDto) {
        DeliveryUpdateRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateServiceDto(id, requestDto);
        DeliveryUpdateResponseServiceDto responseServiceDto = deliveryAdminService.updateDelivery(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/admin/v1/deliveries/{id}/status")
    public ResponseEntity<DeliveryUpdateStatusResponseDto> updateDeliveryStatus(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusRequestDto requestDto) {
        DeliveryUpdateStatusRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateStatusServiceDto(id, requestDto);
        DeliveryUpdateStatusResponseServiceDto responseServiceDto = deliveryAdminService.updateStatusDelivery(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateStatusResponseDto(responseServiceDto));
    }

    @DeleteMapping("/admin/v1/deliveries/{id}")
    public ResponseEntity<Void> deleteDelivery(
            @PathVariable UUID id) {
        DeliveryDeletionRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDeletionServiceDto(id);
        deliveryAdminService.deleteDelivery(requestServiceDto);
        return ResponseEntity.noContent().build();
    }

    // 배송 조회
    @GetMapping("/admin/v1/deliveries/{id}")
    public ResponseEntity<DeliveryDetailResponseDto> getDeliveryDetail(
            @PathVariable UUID id) {
        DeliveryDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDetailServiceDto(id);
        DeliveryDetailResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveryDetail(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toDetailResponseDto(responseServiceDto));
    }

    // 배송 전체 조회 (검색)
    @GetMapping("/admin/v1/deliveries")
    public ResponseEntity<DeliveryResponseDto> searchDelivery(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestBody DeliverySearchRequestDto requestDto) {

        Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
        DeliverySearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toSearchServiceDto(requestDto, customPageable);
        DeliveryResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveriesBySearch(requestServiceDto);

        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toSearchResponseDto(responseServiceDto));
    }

    // 배송 경로 조회
    @GetMapping("/admin/v1/deliveries/routes/{id}")
    public ResponseEntity<DeliveryRouteDetailResponseDto> getDeliveryRouteDetail(
            @PathVariable UUID id) {
        DeliveryRouteDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteDetailServiceDto(id);
        DeliveryRouteDetailResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveryRouteDetail(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toRouteDetailResponseDto(responseServiceDto));
    }

    // 배송 경로 전체 조회 (검색)
    @GetMapping("/admin/v1/deliveries/{id}/routes")
    public ResponseEntity<DeliveryRouteResponseDto> searchDeliveryRoute(
            @PathVariable UUID id,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestBody DeliveryRouteSearchRequestDto requestDto) {

        Pageable customPageable = pageableConfig.customPageable(page, size, sortDirection);
        DeliveryRouteSearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteSearchServiceDto(id, requestDto, customPageable);
        DeliveryRouteResponseServiceDto responseServiceDto = deliveryAdminService.GetDeliveryRoutesBySearch(requestServiceDto);

        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toRouteSearchResponseDto(responseServiceDto));
    }



}
