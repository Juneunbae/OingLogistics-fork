package com.oingmaryho.business.orderservice.application.service.feignclient;


import com.oingmaryho.business.orderservice.presentation.dto.response.ProductDetailsSearchResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;
import java.util.UUID;

@FeignClient(name = "product-service")
public interface ProductClient {
    @GetMapping("/product-service/products/{id}")
    Optional<ProductDetailsSearchResponseDto> getProduct(@PathVariable UUID id);
}