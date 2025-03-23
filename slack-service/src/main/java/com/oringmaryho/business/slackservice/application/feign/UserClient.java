package com.oringmaryho.business.slackservice.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/user-service/users/slackId")
	ResponseEntity<String> getUserSlackIdById(@RequestParam("id") Long id);

}
