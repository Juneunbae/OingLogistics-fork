package com.oingmaryho.business.orderservice.application;

import com.oingmaryho.business.orderservice.application.dto.*;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderDetailUpdateRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderSearchRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderUpdateRequestDto;
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

    OrderServiceDto toOrderServiceDto(UUID orderId);


    @Mapping(target = "orderDetailId", source = "source.orderDetailId")
    @Mapping(target = "quantity", source = "source.quantity")
    @Mapping(target = "price", source = "source.price")
    OrderDetailUpdateServiceDto toOrderDetailUpdateServiceDto(OrderDetailUpdateRequestDto source);

    @Mapping(target = "requests", source = "source.requests")
    OrderUpdateServiceDto toOrderUpdateServiceDto(
        UUID id,
        OrderUpdateRequestDto source,
        List<OrderDetailUpdateServiceDto> orderDetails
    );

    @Mapping(target = "orderDetails", source = "orderDetails")
    OrderDto toOrderDto(Order order, List<OrderDetailDto> orderDetails);

    OrderDetailDto toOrderDetailDto(UUID orderId, OrderDetail orderDetail);

    OrderDetailUpdateDto toOrderDetailUpdateDto(Integer price, Integer quantity);

    OrderUpdateDto toOrderUpdateDto(String requests, Integer totalPrice);
}