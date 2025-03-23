package com.oingmaryho.business.companyservice.application.service.feignClient;

import java.util.Optional;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.oingmaryho.business.companyservice.presentation.dto.response.HubSearchResponseDto;

@FeignClient(name = "hub-service")
public interface HubClient {

	@GetMapping("/hub-service")
	Optional<HubSearchResponseDto> isManagerOfHub(
		@RequestParam("managerId") Long managerId
	);
}
