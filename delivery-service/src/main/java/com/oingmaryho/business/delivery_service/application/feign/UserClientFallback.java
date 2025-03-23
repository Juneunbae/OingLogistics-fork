package com.oingmaryho.business.delivery_service.application.feign;

import com.oingmaryho.business.delivery_service.domain.type.UserRoleType;
import com.oingmaryho.business.delivery_service.exception.DeliveryException;
import com.oingmaryho.business.delivery_service.exception.ErrorCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class UserClientFallback implements UserClient{
    @Override
    public ResponseEntity<UserRoleType> getUserRoleById(Long id) {
        throw new DeliveryException(ErrorCode.USER_SERVICE_UNAVAILABLE);
    }
}
