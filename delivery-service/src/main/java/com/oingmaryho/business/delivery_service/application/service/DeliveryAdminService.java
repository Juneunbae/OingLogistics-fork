package com.oingmaryho.business.delivery_service.application.service;

import com.oingmaryho.business.delivery_service.application.dto.mapper.DeliveryApplicationMapper;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.application.feign.*;
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
import com.oingmaryho.business.delivery_service.infrastructure.repository.DeliveryManagerRepository;
import com.oingmaryho.business.delivery_service.infrastructure.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryAdminService {

    private final CompanyClient companyClient;
    private final HubClient hubClient;
    private final UserClient userClient;

    private final RedisTemplate<String, Object> redisTemplate;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryApplicationMapper deliveryApplicationMapper;


    @Transactional
    public DeliveryCreationResponseServiceDto createDelivery(
            DeliveryCreationRequestServiceDto requestServiceDto) {

        // hub 쪽에 최적 배송 경로 요청
        List<HubPathResponseDto> hubRoutes = Optional.ofNullable(hubClient.getPath(requestServiceDto.hubId(), requestServiceDto.address()))
                .filter(response -> response.getStatusCode().is2xxSuccessful())
                .map(ResponseEntity::getBody)
                .orElseThrow(() -> new DeliveryException(ErrorCode.HUB_ROUTE_NOT_FOUND));

        for (int i = 0; i < hubRoutes.size(); i++) {
            log.info("route{} : departureId({}) arriveId({}) time({}) dist({})", i, hubRoutes.get(i).departureHubId(), hubRoutes.get(i).arriveHubId(), hubRoutes.get(i).hubToHubTime(), hubRoutes.get(i).distance());
        }

//        Delivery delivery = Delivery.builder()
//                .departureHubId(hubRoutes.get(0).departureHubId())
//                .arriveHubId(hubRoutes.get(hubRoutes.size()-1).arriveHubId())
//                .address(requestServiceDto.address())
//                .receiver(requestServiceDto.receiver())
//                .receiverSlackId(requestServiceDto.receiverSlackId())
//                .build();
//
//        // 각 배송 경로에 허브 배송 담당자 배정
//        for (HubPathResponseDto route : hubRoutes) {
//            int routeSequence = 1;      // 배송 경로 상 순번
//            int hubDeliveryManagerSequence = 0;    // TODO Redis에서 조회 (key: hub:delivery:sequence)
//
//            DeliveryManager hubDeliveryManager = deliveryManagerRepository.findByTypeAndSequence(
//                            DeliveryManagerType.HUB_DELIVERY_MANAGER, hubDeliveryManagerSequence % 10)
//                    .orElseThrow(() -> new DeliveryException(ErrorCode.MANAGER_NOT_FOUND));
//
//            hubDeliveryManagerSequence++;
//            DeliveryRoute deliveryRoute = DeliveryRoute.builder()
//                    .delivery(delivery)
//                    .sequence(routeSequence)
//                    .departureHubId(route.departureHubId())
//                    .arriveHubId(route.arriveHubId())
//                    .status(DeliveryRouteStatus.HUB_WAITING)
//                    .estimatedDistance(route.distance())
//                    .estimatedTime(route.hubToHubTime())
//                    .manager(hubDeliveryManager)
//                    .build();
//
//            routeSequence++;
//
//            // 배송 엔티티에 배송 경로 양방향 설정
//            deliveryRoute.addRoute(delivery);
//        }
//
//        // 배송에 업체 배송 담당자 배정
//        int companyDeliveryManagerSequence = 0; // TODO Redis에서 조회 (key: company:delivery:sequence:{hubId})
//        DeliveryManager companyDeliveryManager = deliveryManagerRepository.findByHubIdAndTypeAndSequence(
//                        delivery.getArriveHubId(), DeliveryManagerType.COMPANY_DELIVERY_MANAGER, companyDeliveryManagerSequence % 10)
//                .orElseThrow(() -> new DeliveryException(ErrorCode.MANAGER_NOT_FOUND));
//
//        delivery.update(null,null,null,companyDeliveryManager);
//
//
//        // 배송 저장
//        Delivery savedDelivery = deliveryRepository.save(delivery);

        // 배송 id 반환
        return deliveryApplicationMapper.toCreationResponseServiceDto(UUID.randomUUID());

    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "delivery", key = "#requestServiceDto.id()"),
            @CacheEvict(cacheNames = "deliveries", allEntries = true)
    })
    public DeliveryUpdateResponseServiceDto updateDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryUpdateRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        // managerId로 user 쪽에 수정하려는 manager가 '업체 배송 담당자'인지 유효성 검사
//        ResponseEntity<UserRoleType> userResponse = userClient.getUserRoleById(requestServiceDto.managerId());
//        if (userResponse.getStatusCode().is2xxSuccessful() && userResponse.getBody() != null) {
//            if (!userResponse.getBody().equals(UserRoleType.COMPANY_DELIVERY_MANAGER)) {
//                throw new DeliveryException(ErrorCode.BAD_REQUEST);
//            }
//        }

        // managerId로 DeliveryManager 쪽에 수정하려는 manager가 '업체 배송 담당자'인지 유효성 검사
        DeliveryManager newManager = deliveryManagerRepository.findByManagerIdAndIsDeletedFalse(requestServiceDto.managerId())
                .orElseThrow(() -> new DeliveryException(ErrorCode.MANAGER_NOT_FOUND));
        if (!newManager.getType().equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {
            throw new DeliveryException(ErrorCode.BAD_REQUEST);
        }

        // 수정하려는 manager가 배송의 마지막 허브에 속한 배송 담당자가 아닌 경우
        if (!newManager.getHubId().equals(delivery.getArriveHubId())) {
            throw new DeliveryException(ErrorCode.BAD_REQUEST);
        }

        delivery.update(requestServiceDto.receiver(), requestServiceDto.receiverSlackId(), requestServiceDto.address(), newManager);
        return deliveryApplicationMapper.toUpdateResponseServiceDto(delivery.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "delivery", key = "#requestServiceDto.id()"),
            @CacheEvict(cacheNames = "deliveries", allEntries = true)
    })
    public DeliveryUpdateStatusResponseServiceDto updateStatusDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryUpdateStatusRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        // TODO 고민: 배송 상태와 변경하려는 상태가 같은 경우를 예외로 처리해야 할까?

        delivery.updateStatus(requestServiceDto.status());
        return deliveryApplicationMapper.toUpdateStatusResponseServiceDto(delivery.getId());
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(cacheNames = "delivery", key = "#requestServiceDto.id()"),
            @CacheEvict(cacheNames = "deliveries", allEntries = true)
    })
    public void deleteDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryDeletionRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));
        delivery.softDelete(userId);

    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "delivery", key = "#requestServiceDto.id()")
    public DeliveryResponseServiceDto GetDeliveryDetail(
            Long userId,
            UserRoleType userRole,
            DeliveryDetailRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        return deliveryApplicationMapper.toDeliveryResponseServiceDto(delivery);
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "deliveries")
    public Page<DeliveryResponseServiceDto> GetDeliveriesBySearch(
            Long userId,
            UserRoleType userRole,
            DeliverySearchRequestServiceDto requestServiceDto) {

        DeliverySearchCriteria criteria = createDeliverySearchCriteria(
                requestServiceDto.id(),
                requestServiceDto.orderId(),
                requestServiceDto.orderDetailId(),
                requestServiceDto.hubId(),
                requestServiceDto.companyId(),
                requestServiceDto.status(),
                requestServiceDto.managerId(),
                requestServiceDto.isDeleted()
        );

        Page<Delivery> deliveries = deliveryRepository.searchDelivery(
                criteria,
                requestServiceDto.customPageable());

        return deliveries.map(deliveryApplicationMapper::toDeliveryResponseServiceDto);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "route", key = "#requestServiceDto.id()")
    public DeliveryRouteResponseServiceDto GetDeliveryRouteDetail(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteDetailRequestServiceDto requestServiceDto) {

        DeliveryRoute route = deliveryRepository.findRouteById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.ROUTE_NOT_FOUND));

        return deliveryApplicationMapper.toRouteResponseServiceDto(route);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "routes")
    public Page<DeliveryRouteResponseServiceDto> GetDeliveryRoutesBySearch(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteSearchRequestServiceDto requestServiceDto) {

        DeliveryRouteSearchCriteria criteria = createDeliveryRouteSearchCriteria(
                requestServiceDto.routeId(),
                requestServiceDto.orderId(),
                requestServiceDto.orderDetailId(),
                requestServiceDto.deliveryId(),
                requestServiceDto.departureHubId(),
                requestServiceDto.arriveHubId(),
                requestServiceDto.companyId(),
                requestServiceDto.managerId(),
                requestServiceDto.status(),
                requestServiceDto.isDeleted()
        );


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
    public DeliveryRouteUpdateStatusResponseServiceDto updateRouteStatusDelivery(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteUpdateStatusRequestServiceDto requestServiceDto) {

        DeliveryRoute route = deliveryRepository.findRouteById(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.ROUTE_NOT_FOUND));

        route.changeStatus(requestServiceDto.status());

        if (route.getStatus() == DeliveryRouteStatus.HUB_MOVING) {         // 허브 이동중 상태로 변경 시도하는 경우
            Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(route.getDelivery().getId())
                    .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

            if (delivery.getArriveHubId() == route.getArriveHubId()) {      // 경로 상 출발지 허브가 배송 출발지 허브와 같으면 배송 상태 변경
                delivery.updateStatus(DeliveryStatus.HUB_MOVING);
            }
        }

        if (route.getStatus() == DeliveryRouteStatus.HUB_ARRIVED) {         // 목적지 허브 도착 상태로 변경 시도하는 경우
            Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(route.getDelivery().getId())
                    .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

            if (delivery.getArriveHubId() == route.getArriveHubId()) {      // 경로 상 목적지 허브가 배송 목적지 허브와 같으면 배송 상태 변경
                delivery.updateStatus(DeliveryStatus.HUB_ARRIVED);
            }
        }

        return deliveryApplicationMapper.toUpdateRouteStatusResponseServiceDto(route.getId());

    }

    // 배송 조회 검색 조건 생성 (admin)
    private DeliverySearchCriteria createDeliverySearchCriteria(
            UUID id,
            UUID orderId,
            UUID orderDetailId,
            UUID hubId,
            UUID companyId,
            DeliveryStatus status,
            Long managerId,
            Boolean isDeleted) {

        return DeliverySearchCriteria.builder()
                .id(id)
                .orderId(orderId)
                .orderDetailId(orderDetailId)
                .hubId(hubId)
                .companyId(companyId)
                .status(status)
                .managerId(managerId)   // 배송 담당자 id (Long)
                .isDeleted(isDeleted == null ? null:  // 전체 조회
                        isDeleted ?  Boolean.TRUE : Boolean.FALSE)  // 필터 조건이 들어올 경우 해당하는 결과만 조회
                .build();
    }

    // 배송 경로 조회 검색 조건 생성 (admin)
    private DeliveryRouteSearchCriteria createDeliveryRouteSearchCriteria(
            UUID routeId,
            UUID orderId,
            UUID orderDetailId,
            UUID deliveryId,
            UUID departureHubId,
            UUID arriveHubId,
            UUID companyId,
            Long managerId,
            DeliveryRouteStatus status,
            Boolean isDeleted) {


        return DeliveryRouteSearchCriteria.builder()
                .routeId(routeId)
                .orderId(orderId)
                .orderDetailId(orderDetailId)
                .deliveryId(deliveryId)
                .departureHubId(departureHubId)
                .arriveHubId(arriveHubId)
                .companyId(companyId)
                .managerId(managerId)       // 배송 담당자 id (Long)
                .status(status)
                .isDeleted(isDeleted == null ? null:  // 전체 조회
                        isDeleted ?  Boolean.TRUE : Boolean.FALSE)  // 필터 조건이 들어올 경우 해당하는 결과만 조회
                .build();
    }

}
