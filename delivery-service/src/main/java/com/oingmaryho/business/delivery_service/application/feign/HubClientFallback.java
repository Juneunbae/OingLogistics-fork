package com.oingmaryho.business.delivery_service.application.feign;

import com.oingmaryho.business.delivery_service.exception.DeliveryException;
import com.oingmaryho.business.delivery_service.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class HubClientFallback implements HubClient {

    @Override
    public ResponseEntity<HubSearchResponseDto> getHubByManagerId(Long managerId) {
        throw new DeliveryException(ErrorCode.HUB_SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseEntity<List<HubPathResponseDto>> getPath(UUID departureHubId, String arriveAddress) {
        // TODO 에러 메시지큐 도입해 Order 도메인에 알림?
        throw new DeliveryException(ErrorCode.HUB_SERVICE_UNAVAILABLE);
    }
}
