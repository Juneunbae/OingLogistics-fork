package com.oingmaryho.business.orderservice.presentation;

import com.oingmaryho.business.orderservice.application.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.OrderService;
import com.oingmaryho.business.orderservice.config.pageable.PageableConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {
    private final OrderService orderService;
    private final PageableConfig pageableConfig;
    private final OrderApplicationMapper orderApplicationMapper;
}