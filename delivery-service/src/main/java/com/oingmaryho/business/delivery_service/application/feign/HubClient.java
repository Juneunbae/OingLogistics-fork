package com.oingmaryho.business.delivery_service.application.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "hub-service")
public interface HubClient {

    @GetMapping("/hub-service/{id}")
    ResponseEntity<HubSearchResponseDto> getHub(@PathVariable UUID id);

    @GetMapping("/hub-service/path")
    ResponseEntity<List<HubPathResponseDto>> getPath(HubPathRequestDto requestDto);

}