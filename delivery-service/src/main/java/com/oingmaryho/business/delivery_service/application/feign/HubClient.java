package com.oingmaryho.business.delivery_service.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping
    ResponseEntity<HubSearchResponseDto> getHubByManagerId(
            @RequestParam("managerId") Long managerId);

    @GetMapping("hub-service/path")
    ResponseEntity<List<HubPathResponseDto>> getPath(
            @RequestParam("departureHubId") UUID departureHubId,
            @RequestParam("arriveAddress") String arriveAddress);

}