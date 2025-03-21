package com.oingmaryho.business.delivery_service.application.service;

import com.oingmaryho.business.delivery_service.application.dto.mapper.DeliveryApplicationMapper;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.criteria.DeliveryRouteSearchCriteria;
import com.oingmaryho.business.delivery_service.domain.criteria.DeliverySearchCriteria;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryAdminService {
    private final DeliveryRepository deliveryRepository;
    private final DeliveryApplicationMapper deliveryApplicationMapper;


    @Transactional
    public DeliveryCreationResponseServiceDto createDelivery(Long userId,
                                                             UserRoleType userRole,
                                                             DeliveryCreationRequestServiceDto requestServiceDto) {

        // TODO 1. 배송 경로 요청 (허브 도메인에 요청)
        // GET -> List<HubResponseDto> hubRoutes

        // TODO 2. 배송 담당자 생성 (유저 도메인에 요청) , 소속 업체 id 조회 (업체 도메인에 요청)
        // GET -> List<UserResponseDto> users
        // 허브 배송 담당자: 전체 10명, 업체 배송 담당자: 각 허브당 10명 존재

        // stream().map -> List<DeliveryRoute> routes 생성
        //      순차 배정 방식으로 허브 배송 담당자를 각 배송 경로에 매핑
        //      정렬되어 온다면, index 값을 sequence에 매핑
        // DeliveryManager 생성
        //      허브 배송 담당자, 업체 배송 담당자의 경우, 매핑된 허브 경로의 출발 허브를 hubId로 지정
        //      업체 배송 담당자의 경우, 소속 업체를 companyId로 지정

        // TODO 3. 배송 생성
        // Delivery delivery = DeliveryApplicationMapper.INSTANCE.toDelivery(managerId, hubRoutes[0].getDepartureId(),hubRoutes[hubRoutes.size()-1].getDestinationHubId(),requestServiceDto, routes);
        // deliveryRepository.save(delivery);

        // TODO 4. 배송 UUID 반환
        // 메시지큐로 구현한다면, 주문 도메인에 UUID 메시지 전송
        return null;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "delivery", key = "#requestServiceDto.id()"),
            @CacheEvict(cacheNames = "deliveries", allEntries = true)
    })
    public DeliveryUpdateResponseServiceDto updateDelivery(Long userId,
                                                           UserRoleType userRole,
                                                           DeliveryUpdateRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        DeliveryManager newManager = deliveryRepository.findManagerByIdAndIsDeleted(requestServiceDto.managerId())
                .orElseThrow(() -> new DeliveryException(ErrorCode.MANGER_NOT_FOUND));

        delivery.update(requestServiceDto.receiver(), requestServiceDto.receiverSlackId(), requestServiceDto.address(), newManager);
        return deliveryApplicationMapper.toUpdateResponseServiceDto(delivery.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "delivery", key = "#requestServiceDto.id()"),
            @CacheEvict(cacheNames = "deliveries", allEntries = true)
    })
    public DeliveryUpdateStatusResponseServiceDto updateStatusDelivery(Long userId,
                                                                       UserRoleType userRole,
                                                                       DeliveryUpdateStatusRequestServiceDto requestServiceDto) {
        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        delivery.updateStatus(requestServiceDto.status());
        return deliveryApplicationMapper.toUpdateStatusResponseServiceDto(delivery.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "delivery", key = "#requestServiceDto.id()"),
            @CacheEvict(cacheNames = "deliveries", allEntries = true)
    })
    public void deleteDelivery(Long userId,
                               UserRoleType userRole,
                               DeliveryDeletionRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        deliveryRepository.delete(delivery);

    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "delivery", key = "#requestServiceDto.id()")
    public DeliveryResponseServiceDto GetDeliveryDetail(Long userId,
                                                        UserRoleType userRole,
                                                        DeliveryDetailRequestServiceDto requestServiceDto) {
        Delivery delivery = deliveryRepository.findById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        return deliveryApplicationMapper.toDeliveryResponseServiceDto(delivery);
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "deliveries")
    public Page<DeliveryResponseServiceDto> GetDeliveriesBySearch(Long userId,
                                                                  UserRoleType userRole,
                                                                  DeliverySearchRequestServiceDto requestServiceDto) {
        // TODO 2. userId로 배송 담당자 id 조회
        // TODO 3. 엄체 담당자인 경우 userId로 담당 업체 id 조회

        DeliverySearchCriteria criteria = createDeliverySearchCriteria(userId, requestServiceDto, userRole);
        Page<Delivery> deliveries = deliveryRepository.searchDelivery(
                criteria,
                requestServiceDto.customPageable());

        return deliveries.map(deliveryApplicationMapper::toDeliveryResponseServiceDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "route", key = "#requestServiceDto.id()")
    public DeliveryRouteResponseServiceDto GetDeliveryRouteDetail(Long userId,
                                                                  UserRoleType userRole,
                                                                  DeliveryRouteDetailRequestServiceDto requestServiceDto) {
        DeliveryRoute route = deliveryRepository.findRouteById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        return deliveryApplicationMapper.toRouteResponseServiceDto(route);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "routes")
    public Page<DeliveryRouteResponseServiceDto> GetDeliveryRoutesBySearch(Long userId,
                                                                           UserRoleType userRole,
                                                                           DeliveryRouteSearchRequestServiceDto requestServiceDto) {
        // TODO 2. userId로 배송 담당자 id 조회
        // TODO 3. 엄체 담당자인 경우 userId로 담당 업체 id 조회

        DeliveryRouteSearchCriteria criteria = createDeliveryRouteSearchCriteria(userId, requestServiceDto, userRole);
        Page<DeliveryRoute> routes = deliveryRepository.searchRoute(
                criteria,
                requestServiceDto.customPageable());

        return routes.map(deliveryApplicationMapper::toRouteResponseServiceDto);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "route", key = "#requestServiceDto.id()"),
            @CacheEvict(cacheNames = "routes", allEntries = true)
    })
    public DeliveryRouteUpdateStatusResponseServiceDto updateRouteStatusDelivery(Long userId,
                                                                                 UserRoleType userRole,
                                                                                 DeliveryRouteUpdateStatusRequestServiceDto requestServiceDto) {

        DeliveryRoute route = deliveryRepository.findRouteById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.ROUTE_NOT_FOUND));

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

    // 배송 조회 검색 조건 생성 (admin)
    private DeliverySearchCriteria createDeliverySearchCriteria(Long userId, DeliverySearchRequestServiceDto requestServiceDto, UserRoleType userRole) {
        return DeliverySearchCriteria.builder()
                .userId(userId)
                .hubId(requestServiceDto.hubId())
                .companyId(requestServiceDto.companyId())   // TODO 담당 업체 id
                .managerId(requestServiceDto.managerId())   // TODO 배송 담당자 id
                .managerType(DeliveryManagerType.fromUserRoleType(userRole))
                .isDeleted(requestServiceDto.isDeleted() == null ? null:  // 전체 조회
                        requestServiceDto.isDeleted() ?  Boolean.TRUE : Boolean.FALSE)  // 필터 조건이 들어올 경우 해당하는 결과만 조회
                .build();
    }

    // 배송 경로 조회 검색 조건 생성 (admin)
    private DeliveryRouteSearchCriteria createDeliveryRouteSearchCriteria(Long userId, DeliveryRouteSearchRequestServiceDto requestServiceDto, UserRoleType userRole) {
        return DeliveryRouteSearchCriteria.builder()
                .userId(userId)
                .hubId(requestServiceDto.hubId())
                .companyId(requestServiceDto.companyId())   // TODO 담당 업체 id
                .managerId(requestServiceDto.managerId())   // TODO 배송 담당자 id
                .managerType(DeliveryManagerType.fromUserRoleType(userRole))
                .isDeleted(requestServiceDto.isDeleted() == null ? null:  // 전체 조회
                        requestServiceDto.isDeleted() ?  Boolean.TRUE : Boolean.FALSE)  // 필터 조건이 들어올 경우 해당하는 결과만 조회
                .build();
    }
}
