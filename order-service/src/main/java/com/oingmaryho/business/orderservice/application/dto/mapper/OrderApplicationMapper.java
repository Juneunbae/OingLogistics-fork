package com.oingmaryho.business.orderservice.application.dto.mapper;

import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderCreateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailCreateResponseDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderAdminResponseServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderDetailResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderApplicationMapper {
    @Mapping(target = "orderDetails", source = "orderDetails")
    OrderResponseServiceDto toOrderResponseServiceDto(Order order, List<OrderDetailResponseDto> orderDetails);

    @Mapping(target = "orderDetails", source = "orderDetails")
    OrderAdminResponseServiceDto toOrderAdminResponseDto(Order order, List<OrderDetailResponseDto> orderDetails);

    OrderDetailResponseDto toOrderDetailDto(UUID orderId, OrderDetail orderDetail);

    OrderDetailUpdateRequestServiceDto toOrderDetailUpdateDto(Integer price, Integer quantity);

    OrderUpdateRequestServiceDto toOrderUpdateDto(String requests, Integer totalPrice);

    OrderTotalPriceUpdateRequestServiceDto toOrderTotalPriceUpdateRequestDto(Integer totalPrice);

    ProductQueueRequestDto toProductQueueRequestDto(UUID productId, Integer quantity);

    // @Mapping(target = "orderId", source = "order.id")
    // @Mapping(target = "requesterId", source = "order.requesterId")
    // @Mapping(target = "requesterName", source = "order.requesterName")
    // @Mapping(target = "requesterAddress", source = "order.requesterAddress")
    // @Mapping(target = "requesterSlackId", source = "order.requesterSlackId")
    // @Mapping(target = "orderDetailId", source = "orderDetail.id")
    // @Mapping(target = "recipientId", source = "orderDetail.recipientId")
    // @Mapping(target = "recipientName", source = "orderDetail.recipientName")
    // @Mapping(target = "recipientHubId", source = "orderDetail.recipientHubId")
    DeliveryCreationRequestDto toDeliveryCreationRequestDto(Order order, OrderDetail orderDetail);


    OrderCreateResponseServiceDto toOrderCreateResponseDto(Order order, List<OrderDetailCreateResponseDto> orderDetails);

    @Mapping(target = "orderId", source = "orderDetail.order.id")
    OrderDetailCreateResponseDto toOrderDetailCreateResponseDto(OrderDetail orderDetail);
}