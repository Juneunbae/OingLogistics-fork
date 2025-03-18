package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.DeliveryService;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.config.pageable.PageableUtils;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryRouteSearchRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliverySearchRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryUpdateRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryUpdateStatusRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.DeliveryRouteResponseDto;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/deliveries")
public class DeliveryController {

    private final DeliveryService deliveryService;

    @PutMapping("/{id}")
    public ResponseEntity<DeliveryUpdateResponseDto> updateDelivery(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateRequestDto requestDto) {
        DeliveryUpdateRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateServiceDto(id, requestDto);
        DeliveryUpdateResponseServiceDto responseServiceDto = deliveryService.updateDelivery(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryUpdateStatusResponseDto> updateDeliveryStatus(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusRequestDto requestDto) {
        DeliveryUpdateStatusRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateStatusServiceDto(id, requestDto);
        DeliveryUpdateStatusResponseServiceDto responseServiceDto = deliveryService.updateStatusDelivery(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toUpdateStatusResponseDto(responseServiceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDelivery(
            @PathVariable UUID id) {
        DeliveryDeletionRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDeletionServiceDto(id);
        deliveryService.deleteDelivery(requestServiceDto);
        return ResponseEntity.noContent().build();
    }

    // 배송 조회
    @GetMapping("/{id}")
    public ResponseEntity<DeliveryResponseDto> getDeliveryDetail(
            @PathVariable UUID id) {
        DeliveryDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDetailServiceDto(id);
        DeliveryResponseServiceDto responseServiceDto = deliveryService.GetDeliveryDetail(requestServiceDto);
        return ResponseEntity.ok(DeliveryPresentationMapper.INSTANCE.toDetailResponseDto(responseServiceDto));
    }

    // 배송 전체 조회 (검색)
    @GetMapping
    public ResponseEntity<Page<DeliveryResponseDto>> searchDelivery(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestParam(value = "by", required = false) String by,
            @RequestBody DeliverySearchRequestDto requestDto) {

        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);
        // TODO userId
        DeliverySearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toSearchServiceDto(1L, requestDto, customPageable);
        Page<DeliveryResponseServiceDto> responseServiceDtos = deliveryService.GetDeliveriesBySearch(requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(DeliveryPresentationMapper.INSTANCE::toSearchResponseDto));
    }

    // 배송 경로 조회
    @GetMapping("/routes/{id}")
    public ResponseEntity<DeliveryRouteResponseDto> getDeliveryRouteDetail(
            @PathVariable UUID id) {
        DeliveryRouteDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteDetailServiceDto(id);
        DeliveryRouteResponseServiceDto responseServiceDto = deliveryService.GetDeliveryRouteDetail(requestServiceDto);

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
            @RequestBody DeliveryRouteSearchRequestDto requestDto) {

        Pageable customPageable = PageableUtils.customPageable(page, size, sortDirection, by);

        // TODO userId
        DeliveryRouteSearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteSearchServiceDto(id, 1L, requestDto, customPageable);
        Page<DeliveryRouteResponseServiceDto> responseServiceDtos = deliveryService.GetDeliveryRoutesBySearch(requestServiceDto);

        return ResponseEntity.ok(responseServiceDtos.map(DeliveryPresentationMapper.INSTANCE::toRouteSearchResponseDto));
    }

}
