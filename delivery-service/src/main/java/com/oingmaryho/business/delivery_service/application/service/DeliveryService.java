package com.oingmaryho.business.delivery_service.application.service;

import com.oingmaryho.business.delivery_service.application.dto.mapper.DeliveryApplicationMapper;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.DeliveryRouteSearchCriteria;
import com.oingmaryho.business.delivery_service.domain.DeliverySearchCriteria;
import com.oingmaryho.business.delivery_service.domain.entity.Delivery;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryManager;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryRoute;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryRouteStatus;
import com.oingmaryho.business.delivery_service.domain.type.DeliveryStatus;
import com.oingmaryho.business.delivery_service.domain.type.UserRoleType;
import com.oingmaryho.business.delivery_service.exception.DeliveryException;
import com.oingmaryho.business.delivery_service.exception.ErrorCode;
import com.oingmaryho.business.delivery_service.infrastructure.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryApplicationMapper deliveryApplicationMapper;

    @Transactional
    public DeliveryUpdateResponseServiceDto updateDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryUpdateRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
        // TODO 권한 확인
        DeliveryManager newManager = deliveryRepository.findManagerByIdAndIsDeleted(requestServiceDto.managerId())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_MANGER_NOT_FOUND));

        delivery.update(requestServiceDto.receiver(), requestServiceDto.receiverSlackId(), requestServiceDto.address(), newManager);
        return deliveryApplicationMapper.toUpdateResponseServiceDto(delivery.getId());
    }

    @Transactional
    public DeliveryUpdateStatusResponseServiceDto updateStatusDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryUpdateStatusRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
        // TODO 권한 확인
        delivery.updateStatus(requestServiceDto.status());
        return deliveryApplicationMapper.toUpdateStatusResponseServiceDto(delivery.getId());
    }

    @Transactional
    public void deleteDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryDeletionRequestServiceDto requestServiceDto) {

        // TODO 권한 확인
        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        deliveryRepository.delete(delivery);

    }

    @Transactional(readOnly =true)
    public DeliveryResponseServiceDto GetDeliveryDetail(
            Long userId,
            UserRoleType userRole,
            DeliveryDetailRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        return deliveryApplicationMapper.toDeliveryResponseServiceDto(delivery);
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "deliveries")
    public Page<DeliveryResponseServiceDto> GetDeliveriesBySearch(
            Long userId,
            UserRoleType userRole,
            DeliverySearchRequestServiceDto requestServiceDto) {

        // TODO 1. userId로 사용자 권한 조회
        // TODO 2. userId로 배송 담당자 id 조회
        // TODO 3. 엄체 담당자인 경우 userId로 담당 업체 id 조회

        DeliverySearchCriteria criteria = createDeliverySearchCriteria(userId, requestServiceDto, userRole);
        Page<Delivery> deliveries = deliveryRepository.searchDelivery(
                criteria,
                requestServiceDto.customPageable());

        return deliveries.map(deliveryApplicationMapper::toDeliveryResponseServiceDto);
    }

    @Transactional(readOnly =true)
    public DeliveryRouteResponseServiceDto GetDeliveryRouteDetail(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteDetailRequestServiceDto requestServiceDto) {

        DeliveryRoute route = deliveryRepository.findRouteByIdAndIsDeleted(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_ROUTE_NOT_FOUND));

        return deliveryApplicationMapper.toRouteResponseServiceDto(route);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "routes")
    public Page<DeliveryRouteResponseServiceDto> GetDeliveryRoutesBySearch(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteSearchRequestServiceDto requestServiceDto) {

        // TODO 1. userId로 사용자 권한 조회
        // TODO 2. userId로 배송 담당자 id 조회
        // TODO 3. 엄체 담당자인 경우 userId로 담당 업체 id 조회

        DeliveryRouteSearchCriteria criteria = createDeliveryRouteSearchCriteria(userId, requestServiceDto, userRole);
        Page<DeliveryRoute> routes = deliveryRepository.searchRoute(
                criteria,
                requestServiceDto.customPageable());

        return routes.map(deliveryApplicationMapper::toRouteResponseServiceDto);

    }

    @Transactional
    public DeliveryRouteUpdateStatusResponseServiceDto updateRouteStatusDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteUpdateStatusRequestServiceDto requestServiceDto) {

        DeliveryRoute route = deliveryRepository.findRouteByIdAndIsDeleted(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_ROUTE_NOT_FOUND));

        // TODO 권한 확인
        route.changeStatus(requestServiceDto.status());

        if (route.getStatus() == DeliveryRouteStatus.HUB_ARRIVED) { // 목적지 허브 도착 상태로 변경 시도하는 경우
            Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                    .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

            if (delivery.getDestinationHubId() == route.getDestinationHubId()) {    // 경로 상 목적지 허브가 배송 목적지 허브와 같으면 배송 상태 변경
                delivery.updateStatus(DeliveryStatus.HUB_ARRIVED);
            }
        }

        return deliveryApplicationMapper.toUpdateRouteStatusResponseServiceDto(route.getId());

    }

    // 배송 조회 검색 조건 생성 (일반 사용자)
    private DeliverySearchCriteria createDeliverySearchCriteria(Long userId, DeliverySearchRequestServiceDto requestServiceDto, UserRoleType userRole) {
        return DeliverySearchCriteria.builder()
                .userId(userId)
                .hubId(requestServiceDto.hubId())
                .companyId(requestServiceDto.companyId())   // TODO 담당 업체 id
                .managerId(requestServiceDto.managerId())   // TODO 배송 담당자 id
                .managerType(DeliveryManagerType.fromUserRoleType(userRole))
                .isDeleted(Boolean.FALSE)   // 삭제되지 않은 데이터만 조회
                .build();
    }

    // 배송 경로 조회 검색 조건 생성 (일반 사용자)
    private DeliveryRouteSearchCriteria createDeliveryRouteSearchCriteria(Long userId, DeliveryRouteSearchRequestServiceDto requestServiceDto, UserRoleType userRole) {
        return DeliveryRouteSearchCriteria.builder()
                .userId(userId)
                .hubId(requestServiceDto.hubId())
                .companyId(requestServiceDto.companyId())   // TODO 담당 업체 id
                .managerId(requestServiceDto.managerId())   // TODO 배송 담당자 id
                .managerType(DeliveryManagerType.fromUserRoleType(userRole))
                .isDeleted(Boolean.FALSE)   // 삭제되지 않은 데이터만 조회
                .build();
    }
}
