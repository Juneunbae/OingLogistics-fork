package com.oingmaryho.business.orderservice.application.dto.mapper;

import com.oingmaryho.business.orderservice.application.dto.request.OrderDetailUpdateRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.request.OrderTotalPriceUpdateRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.request.OrderUpdateRequestServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDetailResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderApplicationMapper {
//    @Mapping(target = "id", source = "order.id")
//    @Mapping(target = "recipientId", source = "order.recipientId")
//    @Mapping(target = "recipientName", source = "order.recipientName")
//    @Mapping(target = "status", source = "order.status")
//    @Mapping(target = "totalPrice", source = "order.totalPrice")
//    @Mapping(target = "requests", source = "order.requests")
//    @Mapping(target = "isDeleted", source = "order.isDeleted")
//    @Mapping(target = "deletedBy", source = "order.deletedBy")
//    @Mapping(target = "deletedAt", source = "order.deletedAt")
//    @Mapping(target = "createdBy", source = "order.createdBy")
//    @Mapping(target = "createdAt", source = "order.createdAt")
//    @Mapping(target = "updatedBy", source = "order.updatedBy")
//    @Mapping(target = "updatedAt", source = "order.updatedAt")
    @Mapping(target = "orderDetails", source = "orderDetails")
    OrderResponseServiceDto toOrderResponseServiceDto(Order order, List<OrderDetailResponseDto> orderDetails);

    OrderDetailResponseDto toOrderDetailDto(UUID orderId, OrderDetail orderDetail);

    OrderDetailUpdateRequestServiceDto toOrderDetailUpdateDto(Integer price, Integer quantity);

    OrderUpdateRequestServiceDto toOrderUpdateDto(String requests, Integer totalPrice);

    OrderTotalPriceUpdateRequestServiceDto toOrderTotalPriceUpdateRequestDto(Integer totalPrice);
}