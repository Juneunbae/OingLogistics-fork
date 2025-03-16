package com.oingmaryho.business.orderservice.presentation;

import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDetailDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderPresentationMapper {
    @Mapping(target = "orderDetails", source = "orderDetails")
    OrderDto toOrderDto(Order order, List<OrderDetailDto> orderDetails);

    @Mapping(target = "orderId", source = "orderDetail.order.id")
    OrderDetailDto toOrderDetailDto(OrderDetail orderDetail);
}