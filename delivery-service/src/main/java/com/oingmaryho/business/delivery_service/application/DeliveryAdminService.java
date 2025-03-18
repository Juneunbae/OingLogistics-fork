package com.oingmaryho.business.delivery_service.application;

import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.DeliveryManagerType;
import com.oingmaryho.business.delivery_service.infrastructure.DeliveryRepository;
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

    public DeliveryResponseServiceDto GetDeliveryDetail(DeliveryDetailRequestServiceDto requestDto) {
        return null;
    }

    @Transactional(readOnly =true)
    @Cacheable(cacheNames = "deliveries")
    public Page<DeliveryResponseServiceDto> GetDeliveriesBySearch(DeliverySearchRequestServiceDto requestServiceDto) {
        // TODO 1. userId로 사용자 권한 조회
        // TODO 2. QueryDSL search 쿼리를 호출
        deliveryRepository.searchDelivery(
                requestServiceDto.hubId(),
                requestServiceDto.companyId(),
                requestServiceDto.managerId(),
                DeliveryManagerType.HUB_DELIVERY_MANAGER,   // TODO 3. userId로 조회해 온 사용자의 권한 입력
                requestServiceDto.customPageable());
        return null;
    }

    public DeliveryRouteResponseServiceDto GetDeliveryRouteDetail(DeliveryRouteDetailRequestServiceDto requestServiceDto) {
        return null;
    }

    public Page<DeliveryRouteResponseServiceDto> GetDeliveryRoutesBySearch(DeliveryRouteSearchRequestServiceDto requestServiceDto) {
        return null;
    }
}
