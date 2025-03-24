package com.oingmaryho.business.delivery_service.infrastructure.fallback;

import com.oingmaryho.business.delivery_service.application.feign.CompanyClient;
import com.oingmaryho.business.delivery_service.application.dto.response.CompanyDetailsSearchResponseDto;
import com.oingmaryho.business.delivery_service.exception.DeliveryException;
import com.oingmaryho.business.delivery_service.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CompanyClientFallback implements CompanyClient {
    @Override
    public ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyId(UUID id) {
        throw new DeliveryException(ErrorCode.COMPANY_SERVICE_UNAVAILABLE);
    }

    @Override
    public ResponseEntity<CompanyDetailsSearchResponseDto> getCompanyByManagerId(Long managerId) {
        throw new DeliveryException(ErrorCode.COMPANY_SERVICE_UNAVAILABLE);
    }
}
