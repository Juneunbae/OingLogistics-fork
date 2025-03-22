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
import com.oingmaryho.business.delivery_service.infrastructure.repository.DeliveryManagerRepository;
import com.oingmaryho.business.delivery_service.infrastructure.repository.DeliveryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;
    private final DeliveryManagerRepository deliveryManagerRepository;
    private final DeliveryApplicationMapper deliveryApplicationMapper;

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

        if (userRole == UserRoleType.HUB_MANAGER) {
            // TODO hub 쪽에 HUB_MANAGER 소속 허브 id 조회 -> hubId
            // delivery.getManager().getHubId() != hubId -> throw DeliveryException(ErrorCode.UNAUTHORIZED)
        }

        if (userRole == UserRoleType.COMPANY_DELIVERY_MANAGER) {
            // 업체 배송 담당자가 담당하는 배송이 아닌 경우
            if (!Objects.equals(delivery.getManager().getManagerId(), userId)) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }

            // 업체 배송 담당자가 업체 배송 담당자를 수정하려는 경우
            if (requestServiceDto.managerId() != null) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.HUB_DELIVERY_MANAGER) {
            boolean flag = delivery.getRoutes().stream()
                    .anyMatch(route -> route.getManager().getManagerId().equals(userId));

            // 허브 배송 담당자가 담당하는 배송이 아닌 경우
            if (!flag) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }

            // 허브 배송 담당자가 업체 배송 담당자를 수정하려는 경우
            if (requestServiceDto.managerId() != null) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        DeliveryManager manager = delivery.getManager();
        // manager
        if (requestServiceDto.managerId() != null) {
            // TODO managerId로 user쪽에 실제 존재하는 '업체 배송 담당자'인지 확인 요청 & updateManager() 필드 값 수정
            DeliveryManager newManager = deliveryManagerRepository.findByManagerIdAndIsDeletedFalse(requestServiceDto.managerId())
                    .orElseThrow(() -> new DeliveryException(ErrorCode.MANAGER_NOT_FOUND));

            if (!newManager.getType().equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }

            // TODO companyId로 company 쪽에 managerId가 '업체 배송 담당자'인지 확인 요청
            manager.updateManager(requestServiceDto.managerId(), "slackId", UUID.randomUUID(), UUID.randomUUID());
        }

        delivery.update(requestServiceDto.receiver(), requestServiceDto.receiverSlackId(), requestServiceDto.address(), manager);
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

        if (userRole == UserRoleType.HUB_MANAGER) {
            // TODO hub 쪽에 HUB_MANAGER 소속 허브 id 조회 -> hubId
            // delivery.getManager().getHubId() != hubId -> throw DeliveryException(ErrorCode.UNAUTHORIZED)
        }

        if (userRole == UserRoleType.COMPANY_DELIVERY_MANAGER) {
            // 업체 배송 담당자가 담당하는 배송이 아닌 경우
            if (!Objects.equals(delivery.getManager().getManagerId(), userId)) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.HUB_DELIVERY_MANAGER) {
            boolean flag = delivery.getRoutes().stream()
                    .anyMatch(route -> route.getManager().getManagerId().equals(userId));

            // 허브 배송 담당자가 담당하는 배송이 아닌 경우
            if (!flag) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }

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

        deliveryRepository.delete(delivery);

    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "delivery", key = "#requestServiceDto.id()")
    public DeliveryResponseServiceDto GetDeliveryDetail(
            Long userId,
            UserRoleType userRole,
            DeliveryDetailRequestServiceDto requestServiceDto) {

        Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

        if (userRole == UserRoleType.HUB_MANAGER) {
            // TODO hub 쪽에 HUB_MANAGER 소속 허브 id 조회 -> hubId
            UUID hubId = UUID.randomUUID();
            boolean flag = delivery.getRoutes().stream()
                    .anyMatch(route -> route.getDepartureHubId().equals(hubId));

            // 허브 배송 담당자가 담당하는 배송이 아닌 경우
            if (!flag) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }

        }

        if (userRole == UserRoleType.COMPANY_DELIVERY_MANAGER) {
            // 업체 배송 담당자가 담당하는 배송이 아닌 경우
            if (!Objects.equals(delivery.getManager().getManagerId(), userId)) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.HUB_DELIVERY_MANAGER) {
            boolean flag = delivery.getRoutes().stream()
                    .anyMatch(route -> route.getManager().getManagerId().equals(userId));

            // 허브 배송 담당자가 담당하는 배송이 아닌 경우
            if (!flag) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }

        }

        if (userRole == UserRoleType.COMPANY_MANAGER) {
            // TODO company 쪽에 userId로 담당 업체 id를 조회 -> companyId
            // delivery.getManager().getCompanyId() != companyId -> throw DeliveryException(ErrorCode.UNAUTHORIZED)
        }

        return deliveryApplicationMapper.toDeliveryResponseServiceDto(delivery);
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "deliveries")
    public Page<DeliveryResponseServiceDto> GetDeliveriesBySearch(
            Long userId,
            UserRoleType userRole,
            DeliverySearchRequestServiceDto requestServiceDto) {


        // TODO 허브 담당자인 경우 userId로 담당 허브 id 조회 -> hubId
        // TODO 업체 담당자인 경우 userId로 담당 업체 id 조회 -> companyId

        DeliverySearchCriteria criteria = null;
        if (userRole.equals(UserRoleType.HUB_DELIVERY_MANAGER) || userRole.equals(UserRoleType.COMPANY_DELIVERY_MANAGER)) {
            criteria = createDeliverySearchCriteria(
                    requestServiceDto.orderId(),
                    requestServiceDto.hubId(),
                    requestServiceDto.companyId(),
                    requestServiceDto.status(),
                    userId
            );
        } else {
            criteria = createDeliverySearchCriteria(
                    requestServiceDto.orderId(),
                    requestServiceDto.hubId(),          // TODO hubId
                    requestServiceDto.companyId(),      // TODO companyId
                    requestServiceDto.status(),
                    requestServiceDto.managerId()
            );
        }



        Page<Delivery> deliveries = deliveryRepository.searchDelivery(
                criteria,
                requestServiceDto.customPageable());

        return deliveries.map(deliveryApplicationMapper::toDeliveryResponseServiceDto);
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "route", key = "#requestServiceDto.id()")
    public DeliveryRouteResponseServiceDto GetDeliveryRouteDetail(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteDetailRequestServiceDto requestServiceDto) {

        DeliveryRoute route = deliveryRepository.findRouteByIdAndIsDeleted(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.ROUTE_NOT_FOUND));

        if (userRole == UserRoleType.HUB_MANAGER) {
            // TODO hub 쪽에 HUB_MANAGER 소속 허브 id 조회 -> hubId
            UUID hubId = UUID.randomUUID();
            // 허브 관리자가 담당하는 허브에서 출발하는 배송 경로가 아닌 경우
            if (!route.getDepartureHubId().equals(hubId)) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.COMPANY_DELIVERY_MANAGER) {
            // 업체 배송 담당자가 출발하는 허브와 배송 경로의 목적지 허브가 다른 경우
            if (!route.getArriveHubId().equals(route.getDelivery().getManager().getHubId())) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.HUB_DELIVERY_MANAGER) {
            // 허브 배송 담당자가 출발하는 허브와 배송 경로의 출발지 허브가 다른 경우
            if (!route.getDepartureHubId().equals(route.getManager().getHubId())) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.COMPANY_MANAGER) {
            // TODO company 쪽에 userId로 담당 업체 id를 조회 -> companyId
            UUID companyId = UUID.randomUUID();
            // 업체 담당자가 담당하는 업체 배송 담당자와 관련한 배송 경로가 아닌 경우
            // if (!route.getDelivery().getManager().getCompanyId().equals(companyId)) {
                //throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            // }
            // 업체 배송 담당자가 출발하는 허브와 배송 경로의 목적지 허브가 다른 경우
            if (!route.getArriveHubId().equals(route.getDelivery().getManager().getHubId())) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        return deliveryApplicationMapper.toRouteResponseServiceDto(route);
    }

    @Transactional(readOnly = true)
    @Cacheable(cacheNames = "routes")
    public Page<DeliveryRouteResponseServiceDto> GetDeliveryRoutesBySearch(
            Long userId,
            UserRoleType userRole,
            DeliveryRouteSearchRequestServiceDto requestServiceDto) {


        // TODO 허브 관리자인 경우 userId로 담당 허브 id 조회 -> hubId
        // TODO 업체 담당자인 경우 userId로 담당 업체 id 조회 -> companyId
        DeliveryRouteSearchCriteria criteria = null;
        if (userRole.equals(UserRoleType.HUB_DELIVERY_MANAGER) || userRole.equals(UserRoleType.COMPANY_DELIVERY_MANAGER)) {
            criteria = createDeliveryRouteSearchCriteria(
                    requestServiceDto.routeId(),
                    requestServiceDto.deliveryId(),
                    requestServiceDto.departureHubId(),
                    requestServiceDto.arriveHubId(),
                    requestServiceDto.companyId(),
                    userId,
                    requestServiceDto.status()
            );
        } else {
            criteria = createDeliveryRouteSearchCriteria(
                    requestServiceDto.routeId(),
                    requestServiceDto.deliveryId(),
                    requestServiceDto.departureHubId(), // TODO hubId
                    requestServiceDto.arriveHubId(),
                    requestServiceDto.companyId(),      // TODO companyId
                    requestServiceDto.managerId(),
                    requestServiceDto.status()
            );
        }

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

        DeliveryRoute route = deliveryRepository.findRouteByIdAndIsDeleted(requestServiceDto.id())
                .orElseThrow(() -> new DeliveryException(ErrorCode.ROUTE_NOT_FOUND));

        if (userRole == UserRoleType.HUB_MANAGER) {
            // TODO hub 쪽에 HUB_MANAGER 소속 허브 id 조회 -> hubId
            UUID hubId = UUID.randomUUID();
            // 허브 관리자가 담당하는 허브에서 출발하는 배송 경로가 아닌 경우
            if (!route.getDepartureHubId().equals(hubId)) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.COMPANY_DELIVERY_MANAGER) {
            // 업체 배송 담당자가 출발하는 허브와 배송 경로의 목적지 허브가 다른 경우
            if (!route.getArriveHubId().equals(route.getDelivery().getManager().getHubId())) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.HUB_DELIVERY_MANAGER) {
            // 허브 배송 담당자가 출발하는 허브와 배송 경로의 출발지 허브가 다른 경우
            if (!route.getDepartureHubId().equals(route.getManager().getHubId())) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        if (userRole == UserRoleType.COMPANY_MANAGER) {
            // TODO company 쪽에 userId로 담당 업체 id를 조회 -> companyId
            UUID companyId = UUID.randomUUID();
            // 업체 담당자가 담당하는 업체 배송 담당자와 관련한 배송 경로가 아닌 경우
            // if (!route.getDelivery().getManager().getCompanyId().equals(companyId)) {
            //throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            // }
            // 업체 배송 담당자가 출발하는 허브와 배송 경로의 목적지 허브가 다른 경우
            if (!route.getArriveHubId().equals(route.getDelivery().getManager().getHubId())) {
                throw new DeliveryException(ErrorCode.UNAUTHORIZED);
            }
        }

        route.changeStatus(requestServiceDto.status());

        if (route.getStatus() == DeliveryRouteStatus.HUB_ARRIVED) { // 목적지 허브 도착 상태로 변경 시도하는 경우
            Delivery delivery = deliveryRepository.findByIdAndIsDeletedFalse(route.getDelivery().getId())
                    .orElseThrow(() -> new DeliveryException(ErrorCode.DELIVERY_NOT_FOUND));

            if (delivery.getArriveHubId() == route.getArriveHubId()) {    // 경로 상 목적지 허브가 배송 목적지 허브와 같으면 배송 상태 변경
                delivery.updateStatus(DeliveryStatus.HUB_ARRIVED);
            }
        }

        return deliveryApplicationMapper.toUpdateRouteStatusResponseServiceDto(route.getId());

    }

    // 배송 조회 검색 조건 생성 (일반 사용자)
    private DeliverySearchCriteria createDeliverySearchCriteria(
            UUID orderId,
            UUID hubId,
            UUID companyId,
            DeliveryStatus status,
            Long managerId) {

        return DeliverySearchCriteria.builder()
                .orderId(orderId)
                .hubId(hubId)
                .companyId(companyId)
                .status(status)
                .managerId(managerId)       // 배송 담당자 id (Long)
                .isDeleted(Boolean.FALSE)   // 삭제되지 않은 데이터만 조회
                .build();
    }

    // 배송 경로 조회 검색 조건 생성 (일반 사용자)
    private DeliveryRouteSearchCriteria createDeliveryRouteSearchCriteria(
            UUID routeId,
            UUID deliveryId,
            UUID departureHubId,
            UUID arriveHubId,
            UUID companyId,
            Long managerId,
            DeliveryRouteStatus status) {


        return DeliveryRouteSearchCriteria.builder()
                .routeId(routeId)
                .deliveryId(deliveryId)
                .departureHubId(departureHubId)
                .arriveHubId(arriveHubId)
                .companyId(companyId)
                .managerId(managerId)       // 배송 담당자 id (Long)
                .status(status)
                .isDeleted(Boolean.FALSE)   // 삭제되지 않은 데이터만 조회
                .build();
    }
}
