package com.oingmaryho.business.orderservice.application.service.feignclient;

import com.oingmaryho.business.orderservice.application.dto.response.HubSearchResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hub-service")
public interface HubClient {
    @GetMapping("/hub-service")
    HubSearchResponseDto getHubById(@RequestParam(value = "managerId") Long managerId);
}