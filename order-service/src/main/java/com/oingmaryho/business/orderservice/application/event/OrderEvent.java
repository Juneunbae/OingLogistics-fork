package com.oingmaryho.business.orderservice.application.event;

import com.oingmaryho.business.orderservice.domain.Order;

public record OrderEvent(
    Order order
) {
}