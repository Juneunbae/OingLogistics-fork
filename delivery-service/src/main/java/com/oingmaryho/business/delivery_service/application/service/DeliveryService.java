package com.oingmaryho.business.delivery_service.application.service;

import com.oingmaryho.business.delivery_service.application.dto.mapper.DeliveryApplicationMapper;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.Delivery;
import com.oingmaryho.business.delivery_service.domain.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.domain.DeliveryRoute;
import com.oingmaryho.business.delivery_service.domain.UserRoleType;
import com.oingmaryho.business.delivery_service.exception.DeliveryException;
import com.oingmaryho.business.delivery_service.exception.ErrorCode;
import com.oingmaryho.business.delivery_service.infrastructure.DeliveryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    @Transactional
    public DeliveryUpdateResponseServiceDto updateDelivery(Long userId,
                                                           UserRoleType userRole,
                                                           DeliveryUpdateRequestServiceDto requestServiceDto) {
        Delivery delivery = deliveryRepository.findById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
        // TODO 권한 확인

        delivery.update(requestServiceDto);
        return DeliveryApplicationMapper.INSTANCE.toUpdateResponseServiceDto(delivery.getId());
    }

    @Transactional
    public DeliveryUpdateStatusResponseServiceDto updateStatusDelivery(Long userId,
                                                                       UserRoleType userRole,
                                                                       DeliveryUpdateStatusRequestServiceDto requestServiceDto) {
        Delivery delivery = deliveryRepository.findById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
        // TODO 권한 확인
        delivery.updateStatus(requestServiceDto);
        return DeliveryApplicationMapper.INSTANCE.toUpdateStatusResponseServiceDto(delivery.getId());
    }

    @Transactional
    public void deleteDelivery(Long userId,
                               UserRoleType userRole,
                               DeliveryDeletionRequestServiceDto requestServiceDto) {

        // TODO 권한 확인

        Delivery delivery = deliveryRepository.findById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        deliveryRepository.delete(delivery);

    }

    @Transactional(readOnly =true)
    public DeliveryResponseServiceDto GetDeliveryDetail(Long userId,
                                                        UserRoleType userRole,
                                                        DeliveryDetailRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        return DeliveryApplicationMapper.INSTANCE.toDeliveryResponseServiceDto(delivery);
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "deliveries")
    public Page<DeliveryResponseServiceDto> GetDeliveriesBySearch(Long userId,
                                                                  UserRoleType userRole,
                                                                  DeliverySearchRequestServiceDto requestServiceDto) {
        // TODO 1. userId로 사용자 권한 조회
        // TODO 2. userId로 배송 담당자 id 조회
        // TODO 3. 엄체 담당자인 경우 userId로 담당 업체 id 조회
        Page<Delivery> deliveries = deliveryRepository.searchDelivery(
                requestServiceDto.hubId(),
                requestServiceDto.companyId(),              // TODO 3. 담당 업체 id
                requestServiceDto.managerId(),              // TODO 2. 배송 담당자 id
                DeliveryManagerType.HUB_DELIVERY_MANAGER,   // TODO 1. 사용자 권한
                requestServiceDto.customPageable());
        return deliveries.map(DeliveryApplicationMapper.INSTANCE::toDeliveryResponseServiceDto);
    }

    @Transactional(readOnly =true)
    public DeliveryRouteResponseServiceDto GetDeliveryRouteDetail(Long userId,
                                                                  UserRoleType userRole,
                                                                  DeliveryRouteDetailRequestServiceDto requestServiceDto) {
        DeliveryRoute route = deliveryRepository.findByRouteId(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        return DeliveryApplicationMapper.INSTANCE.toRouteResponseServiceDto(route);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "routes")
    public Page<DeliveryRouteResponseServiceDto> GetDeliveryRoutesBySearch(Long userId,
                                                                           UserRoleType userRole,
                                                                           DeliveryRouteSearchRequestServiceDto requestServiceDto) {
        // TODO 1. userId로 사용자 권한 조회
        // TODO 2. userId로 배송 담당자 id 조회
        // TODO 3. 엄체 담당자인 경우 userId로 담당 업체 id 조회
        Page<DeliveryRoute> routes = deliveryRepository.searchRoute(
                requestServiceDto.hubId(),
                requestServiceDto.companyId(),              // TODO 3. 담당 업체 id
                requestServiceDto.managerId(),              // TODO 2. 배송 담당자 id
                DeliveryManagerType.HUB_DELIVERY_MANAGER,   // TODO 1. 사용자 권한
                requestServiceDto.customPageable());

        return routes.map(DeliveryApplicationMapper.INSTANCE::toRouteResponseServiceDto);

    }
}
