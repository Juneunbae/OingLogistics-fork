package com.oingmaryho.business.companyservice.application.service.feignClient;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oingmaryho.business.common.domain.type.UserRoleType;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/user-service/users/role")
	Optional<UserRoleType> userFeignServiceGetRoleById(
		@RequestParam(name = "id", required = true) Long id
	);

}
