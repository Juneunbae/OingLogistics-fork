package com.oingmaryho.business.orderservice.application;

import com.oingmaryho.business.orderservice.application.dto.OrderServiceDto;
import com.oingmaryho.business.orderservice.application.dto.OrdersServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderSearchRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDetailDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderApplicationMapper {
    @Mapping(target = "productName", source = "orderSearchRequestDto.productName")
    @Mapping(target = "recipientName", source = "orderSearchRequestDto.recipientName")
    @Mapping(target = "requesterName", source = "orderSearchRequestDto.requesterName")
    @Mapping(target = "isDeleted", source = "orderSearchRequestDto.isDeleted")
    OrdersServiceDto toOrdersServiceDto(
        OrderSearchRequestDto orderSearchRequestDto,
        Pageable customPageable
    );

    @Mapping(target = "orderId", source = "orderDetail.order.id")
    OrderDetailDto toOrderDetailDto(OrderDetail orderDetail);

    @Mapping(target = "orderDetails", source = "orderDetails")
    OrderDto toOrderDto(Order order, List<OrderDetailDto> orderDetails);

    OrderServiceDto toOrderServiceDto(UUID orderId);
}