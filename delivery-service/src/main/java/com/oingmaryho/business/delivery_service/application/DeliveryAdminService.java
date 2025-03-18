package com.oingmaryho.business.delivery_service.application;

import com.oingmaryho.business.delivery_service.application.dto.mapper.DeliveryApplicationMapper;
import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.Delivery;
import com.oingmaryho.business.delivery_service.domain.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.domain.DeliveryRoute;
import com.oingmaryho.business.delivery_service.infrastructure.DeliveryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DeliveryAdminService {
    private final DeliveryRepository deliveryRepository;

    public DeliveryCreationResponseServiceDto createDelivery(DeliveryCreationRequestServiceDto requestServiceDto) {

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

    public DeliveryUpdateResponseServiceDto updateDelivery(DeliveryUpdateRequestServiceDto requestServiceDto) {
        return null;
    }

    public DeliveryUpdateStatusResponseServiceDto updateStatusDelivery(DeliveryUpdateStatusRequestServiceDto requestServiceDto) {
        return null;
    }

    public void deleteDelivery(DeliveryDeletionRequestServiceDto requestServiceDto) {

    }

    public DeliveryResponseServiceDto GetDeliveryDetail(DeliveryDetailRequestServiceDto requestServiceDto) {
        Delivery delivery = deliveryRepository.findById(requestServiceDto.id())
                .orElseThrow(() -> new EntityNotFoundException("delivery not found"));  // TODO 커스텀 예외 처리

        return DeliveryApplicationMapper.INSTANCE.toDeliveryResponseServiceDto(delivery);
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "deliveries")
    public Page<DeliveryResponseServiceDto> GetDeliveriesBySearch(DeliverySearchRequestServiceDto requestServiceDto) {
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

    public DeliveryRouteResponseServiceDto GetDeliveryRouteDetail(DeliveryRouteDetailRequestServiceDto requestServiceDto) {
        DeliveryRoute route = deliveryRepository.findByRouteId(requestServiceDto.id())
                .orElseThrow(() -> new EntityNotFoundException("delivery route not found"));

        return DeliveryApplicationMapper.INSTANCE.toRouteResponseServiceDto(route);   // TODO 커스텀 예외 처리
    }

    public Page<DeliveryRouteResponseServiceDto> GetDeliveryRoutesBySearch(DeliveryRouteSearchRequestServiceDto requestServiceDto) {
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
