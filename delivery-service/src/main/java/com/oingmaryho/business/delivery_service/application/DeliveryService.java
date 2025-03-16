package com.oingmaryho.business.delivery_service.application;

import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.infrastructure.DeliveryRepository;
import com.oingmaryho.business.delivery_service.presentation.dto.response.DeliveryDetailResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeliveryService {

    private final DeliveryRepository deliveryRepository;

    public DeliveryUpdateResponseServiceDto updateDelivery(DeliveryUpdateRequestServiceDto requestServiceDto) {
        return null;
    }

    public DeliveryUpdateStatusResponseServiceDto updateStatusDelivery(DeliveryUpdateStatusRequestServiceDto requestServiceDto) {
        return null;
    }

    public void deleteDelivery(DeliveryDeletionRequestServiceDto requestServiceDto) {

    }

    public DeliveryDetailResponseServiceDto GetDeliveryDetail(DeliveryDetailRequestServiceDto requestDto) {
        return null;
    }

    public DeliveryResponseServiceDto GetDeliveriesBySearch(DeliverySearchRequestServiceDto requestServiceDto) {
        return null;
    }

    public DeliveryRouteDetailResponseServiceDto GetDeliveryRouteDetail(DeliveryRouteDetailRequestServiceDto requestServiceDto) {
        return null;
    }

    public DeliveryRouteResponseServiceDto GetDeliveryRoutesBySearch(DeliveryRouteSearchRequestServiceDto requestServiceDto) {
        return null;
    }
}
