package com.oingmaryho.business.delivery_service.application.service;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import com.oingmaryho.business.delivery_service.application.DeliveryLockHelper;
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
import com.oingmaryho.business.delivery_service.exception.DeliveryException;
import com.oingmaryho.business.delivery_service.exception.ErrorCode;
import com.oingmaryho.business.delivery_service.domain.repository.DeliveryManagerRepository;
import com.oingmaryho.business.delivery_service.domain.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeliveryAdminService {

    private final HubClient hubClient;
    private final UserClient userClient;

    private final RedisTemplate<String, Object> redisTemplate;

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryApplicationMapper deliveryApplicationMapper;

    // --- helper --- //
    private final DeliveryLockHelper deliveryLockHelper;


    @Transactional
    public DeliveryCreationResponseServiceDto createDelivery(
            DeliveryCreationRequestServiceDto requestServiceDto) {

        // 1. 최적 경로 조회
        List<HubPathResponseDto> hubRoutes = Optional.ofNullable(
                hubClient.getPath(requestServiceDto.hubId(), requestServiceDto.address()).getBody()
        ).orElseThrow(() -> new DeliveryException(ErrorCode.HUB_PATH_NOT_FOUND));


        // -----logging----- //
        for (int i = 0; i < hubRoutes.size(); i++) {
            log.info("route{} : departureId({}) arriveId({}) time({}) dist({})",
                    i,
                    hubRoutes.get(i).departureHubId(),
                    hubRoutes.get(i).arriveHubId(),
                    hubRoutes.get(i).hubToHubTime(),
                    hubRoutes.get(i).distance());
        }

        // 2. 배송 객체 생성
        Delivery delivery = Delivery.builder()
                .orderId(requestServiceDto.orderId())
                .orderDetailId(requestServiceDto.orderDetailId())
                .companyId(requestServiceDto.companyId())
                .departureHubId(hubRoutes.get(0).departureHubId())
                .arriveHubId(hubRoutes.get(hubRoutes.size()-1).arriveHubId())
                .address(requestServiceDto.address())
                .receiver(requestServiceDto.receiver())
                .receiverSlackId(requestServiceDto.receiverSlackId())
                .build();

        String hubDeliveryManagerSequenceKey = "hub:delivery:sequence";

        // 3. 현재 허브 배송 담당자 sequence 값 백업
        Object backupValue = redisTemplate.opsForValue().get(hubDeliveryManagerSequenceKey);
        int backupHubDeliveryManagerSequence = Optional.ofNullable(backupValue)
                .map(Object::toString)
                .map(Integer::parseInt)
                .orElse(0);

        // redis 초기값 보장
        redisTemplate.opsForValue().setIfAbsent(hubDeliveryManagerSequenceKey, 0);
//        if (!redisTemplate.hasKey(hubDeliveryManagerSequenceKey)) {
//            redisTemplate.opsForValue().set(hubDeliveryManagerSequenceKey, 0);
//        }


        // 락
        int routeSequence = 0;
        String hubLockValue = UUID.randomUUID().toString();
        boolean hubLocked = deliveryLockHelper.tryHubManagerLock(hubLockValue, 5);

        if (!hubLocked) {
            throw new DeliveryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String hubNames = hubRoutes.get(0).departureHubName();

        // 4. 허브 배송 담당자 배정
        try {
            for (HubPathResponseDto route : hubRoutes) {
                // 4-1. 현재 배정해야 할 허브 배송 담당자 sequence 조회
                int hubDeliveryManagerSequence = Optional.ofNullable(redisTemplate.opsForValue().get(hubDeliveryManagerSequenceKey))
                        .map(Object::toString)
                        .map(Integer::parseInt)
                        .orElse(0);

                // 4-2. 현재 sequence에 해당하는 허브 배송 담당자를 조회 (전체 물류 시스템에 10명 - 순차 배정)
                DeliveryManager hubDeliveryManager = deliveryManagerRepository.findByTypeAndSequence(
                                DeliveryManagerType.HUB_DELIVERY_MANAGER,
                                hubDeliveryManagerSequence % 10)
                        .orElseThrow(() -> new DeliveryException(ErrorCode.MANAGER_NOT_FOUND));

                // 4-3. redis 허브 배송 담당자 sequence 업데이트
                redisTemplate.opsForValue().increment(hubDeliveryManagerSequenceKey);

                // 4-4. 배송 경로 생성
                DeliveryRoute deliveryRoute = DeliveryRoute.builder()
                        .delivery(delivery)
                        .sequence(routeSequence++)
                        .departureHubId(route.departureHubId())
                        .arriveHubId(route.arriveHubId())
                        .status(DeliveryRouteStatus.HUB_WAITING)
                        .estimatedDistance(route.distance())
                        .estimatedTime(route.hubToHubTime())
                        .manager(hubDeliveryManager)
                        .build();

                deliveryRoute.addRoute(delivery);
                // 4-5. 경유 허브 이름 저장
                hubNames = hubNames + "," +route.arriveHubName();
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 예외 발생 시 Redis 값 복원
            redisTemplate.opsForValue().set(hubDeliveryManagerSequenceKey, backupHubDeliveryManagerSequence);
            // 예외 다시 던져서 트랜잭션 롤백 유도
            throw new DeliveryException(ErrorCode.DELIVERY_MANAGER_NOT_ASSIGNED);
        } finally {
            deliveryLockHelper.releaseHubManagerLock(hubLockValue);
        }

        UUID arriveHubId = hubRoutes.get(hubRoutes.size() - 1).arriveHubId();

        // 5. 배송에 업체 배송 담당자 배정
        String companyDeliveryManagerSequenceKey = "company:delivery:sequence:" + arriveHubId;
        // redis 초기값 보장
        redisTemplate.opsForValue().setIfAbsent(companyDeliveryManagerSequenceKey, 0);
//        if (!redisTemplate.hasKey(companyDeliveryManagerSequenceKey)) {
//            redisTemplate.opsForValue().set(companyDeliveryManagerSequenceKey, 0);
//        }

        // 락
        String companyLockValue = UUID.randomUUID().toString();
        boolean companyLocked = deliveryLockHelper.tryCompanyManagerLock(arriveHubId, companyLockValue, 5);

        if (!companyLocked) {
            throw new DeliveryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

        String companyDeliveryManagerSlackId = null;  // 업체 배송 담당자 배정 sequence 업데이트 전 예외가 발생했을 때 처리를 위한 flag
        try {
            // 5-1. 현재 배정해야 할 업체 배송 담당자 sequence 조회
            int companySequence = Optional.ofNullable(redisTemplate.opsForValue().get(companyDeliveryManagerSequenceKey))
                    .map(Object::toString)
                    .map(Integer::parseInt)
                    .orElse(0);

            // 5-2. 현재 sequence에 해당하는 업체 배송 담당자를 조회 (배송 경로 기준 마지막 경로의 도착지 허브에 있는 업체 배송 담당자 10명 - 순차 배정)
            DeliveryManager companyDeliveryManager = deliveryManagerRepository.findByHubIdAndTypeAndSequence(
                            arriveHubId, DeliveryManagerType.COMPANY_DELIVERY_MANAGER, companySequence % 10)
                    .orElseThrow(() -> new DeliveryException(ErrorCode.MANAGER_NOT_FOUND));

            companyDeliveryManagerSlackId = companyDeliveryManager.getSlackId();

            // 5-3. 업체 배송 담당자 배송 엔티티에 연결
            delivery.update(null, null, null, companyDeliveryManager);

            // 5-4. redis 업체 배송 담당자 sequence 업데이트
            redisTemplate.opsForValue().increment(companyDeliveryManagerSequenceKey);

        } catch (DeliveryException e) {
            throw new DeliveryException(ErrorCode.DELIVERY_MANAGER_NOT_ASSIGNED);
        } catch (Exception e) {
            throw new DeliveryException(ErrorCode.INTERNAL_SERVER_ERROR);
        } finally {
            deliveryLockHelper.releaseCompanyManagerLock(arriveHubId, companyLockValue);
        }

        try {
            // 6. 배송 생성
            Delivery savedDelivery = deliveryRepository.save(delivery);

            // 7. 업체 배송 담당자 이름 조회
            String companyDeliveryManagerName = Optional.ofNullable(userClient.getUserName(delivery.getManager().getManagerId()).getBody())
                    .orElseThrow(() -> new DeliveryException(ErrorCode.USER_NAME_NOT_FOUND));

            // 8. 배송 정보 반환
            return deliveryApplicationMapper.toCreationResponseServiceDto(
                    savedDelivery.getOrderId(),
                    savedDelivery.getOrderDetailId(),
                    savedDelivery.getId(),
                    hubRoutes.get(0).departureHubName(),
                    hubNames,
                    hubRoutes.get(hubRoutes.size()-1).arriveHubName(),
                    companyDeliveryManagerName,
                    companyDeliveryManagerSlackId);
        } catch (DeliveryException e) {
            redisTemplate.opsForValue().increment(companyDeliveryManagerSequenceKey, -1);
            throw new DeliveryException(ErrorCode.DELIVERY_MANAGER_NOT_ASSIGNED);
        } catch (Exception e) {
            redisTemplate.opsForValue().increment(companyDeliveryManagerSequenceKey, -1);
            throw new DeliveryException(ErrorCode.INTERNAL_SERVER_ERROR);
        }

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
        UserRoleType userRoleType = (UserRoleType) Optional.ofNullable(
                userClient.getUserRoleById(requestServiceDto.managerId()).getBody()
        ).orElseThrow(() -> new DeliveryException(ErrorCode.USER_ROLE_NOT_FOUND));


        if (!userRoleType.equals(UserRoleType.COMPANY_DELIVERY_MANAGER)) {
            throw new DeliveryException(ErrorCode.BAD_REQUEST);
        }

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

        // 배송 상태와 변경하려는 상태가 같은 경우
        if (delivery.getStatus().equals(requestServiceDto.status())) {
            throw new DeliveryException(ErrorCode.BAD_REQUEST);
        }

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

        // 배송 경로 상태와 변경하려는 상태가 같은 경우
        if (route.getStatus().equals(requestServiceDto.status())) {
            throw new DeliveryException(ErrorCode.BAD_REQUEST);
        }

        route.changeStatus(requestServiceDto.status());

        // 허브 이동중 상태로 변경 시도하는 경우
        if (route.getStatus() == DeliveryRouteStatus.HUB_MOVING) {
            Delivery delivery = route.getDelivery();

            if (delivery.getIsDeleted()) {
                throw new DeliveryException(ErrorCode.BAD_REQUEST);
            }

            // 경로 상 출발지 허브가 배송 출발지 허브와 같으면 배송 상태 변경
            if (delivery.getArriveHubId() == route.getArriveHubId()) {
                delivery.updateStatus(DeliveryStatus.HUB_MOVING);
            }
        }
        // 목적지 허브 도착 상태로 변경 시도하는 경우
        if (route.getStatus() == DeliveryRouteStatus.HUB_ARRIVED) {
            Delivery delivery = route.getDelivery();

            if (delivery.getIsDeleted()) {
                throw new DeliveryException(ErrorCode.BAD_REQUEST);
            }

            // 경로 상 목적지 허브가 배송 목적지 허브와 같으면 배송 상태 변경
            if (delivery.getArriveHubId() == route.getArriveHubId()) {
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
                .managerId(managerId)                               // 배송 담당자 id (Long)
                .isDeleted(isDeleted == null ? null:                // 전체 조회
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
                .managerId(managerId)                               // 배송 담당자 id (Long)
                .status(status)
                .isDeleted(isDeleted == null ? null:                // 전체 조회
                        isDeleted ?  Boolean.TRUE : Boolean.FALSE)  // 필터 조건이 들어올 경우 해당하는 결과만 조회
                .build();
    }

}
