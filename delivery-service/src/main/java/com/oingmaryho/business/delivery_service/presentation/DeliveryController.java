package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.DeliveryApplicationMapper;
import com.oingmaryho.business.delivery_service.application.DeliveryService;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryRouteSearchRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliverySearchRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryUpdateRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryUpdateStatusRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.DeliveryRouteResponseDto;

import lombok.RequiredArgsConstructor;
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
        return ResponseEntity.ok(DeliveryApplicationMapper.INSTANCE.toUpdateResponseDto(responseServiceDto));
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<DeliveryUpdateStatusResponseDto> updateDeliveryStatus(
            @PathVariable UUID id,
            @RequestBody DeliveryUpdateStatusRequestDto requestDto) {
        DeliveryUpdateStatusRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toUpdateStatusServiceDto(id, requestDto);
        DeliveryUpdateStatusResponseServiceDto responseServiceDto = deliveryService.updateStatusDelivery(requestServiceDto);
        return ResponseEntity.ok(DeliveryApplicationMapper.INSTANCE.toUpdateStatusResponseDto(responseServiceDto));
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
    public ResponseEntity<DeliveryDetailResponseDto> getDeliveryDetail(
            @PathVariable UUID id) {
        DeliveryDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toDetailServiceDto(id);
        DeliveryDetailResponseServiceDto responseServiceDto = deliveryService.GetDeliveryDetail(requestServiceDto);
        return ResponseEntity.ok(DeliveryApplicationMapper.INSTANCE.toDetailResponseDto(responseServiceDto));
    }

    // 배송 전체 조회 (검색)
    @GetMapping
    public ResponseEntity<DeliveryResponseDto> searchDelivery(
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestBody DeliverySearchRequestDto requestDto) {
        DeliverySearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toSearchServiceDto(requestDto);
        DeliveryResponseServiceDto responseServiceDto = deliveryService.GetDeliveriesBySearch(requestServiceDto);
        return ResponseEntity.ok(DeliveryApplicationMapper.INSTANCE.toSearchResponseDto(responseServiceDto));
    }

    // 배송 경로 조회
    @GetMapping("/routes/{id}")
    public ResponseEntity<DeliveryRouteDetailResponseDto> getDeliveryRouteDetail(
            @PathVariable UUID id) {
        DeliveryRouteDetailRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteDetailServiceDto(id);
        DeliveryRouteDetailResponseServiceDto responseServiceDto = deliveryService.GetDeliveryRouteDetail(requestServiceDto);
        return ResponseEntity.ok(DeliveryApplicationMapper.INSTANCE.toRouteDetailResponseDto(responseServiceDto));
    }

    // 배송 경로 전체 조회 (검색)
    @GetMapping("/{id}/routes")
    public ResponseEntity<DeliveryRouteResponseDto> searchDeliveryRoute(
            @PathVariable UUID id,
            @RequestParam(value = "page", required = false, defaultValue = "1") Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "sortDirection", required = false) String sortDirection,
            @RequestBody DeliveryRouteSearchRequestDto requestDto) {

        DeliveryRouteSearchRequestServiceDto requestServiceDto = DeliveryPresentationMapper.INSTANCE.toRouteSearchServiceDto(id, requestDto);
        DeliveryRouteResponseServiceDto responseServiceDto = deliveryService.GetDeliveryRoutesBySearch(requestServiceDto);
        return ResponseEntity.ok(DeliveryApplicationMapper.INSTANCE.toRouteSearchResponseDto(responseServiceDto));
    }

}
