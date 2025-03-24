package com.oingmaryho.business.delivery_service.application.feign;

import com.oingmaryho.business.common.domain.type.UserRoleType;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("user-service/users/role")
    ResponseEntity<UserRoleType> getUserRoleById(
            @RequestParam("id") Long id);


    @GetMapping("user-service/name")
    ResponseEntity<String> getUserName(
            @RequestParam("id") Long id);

}
