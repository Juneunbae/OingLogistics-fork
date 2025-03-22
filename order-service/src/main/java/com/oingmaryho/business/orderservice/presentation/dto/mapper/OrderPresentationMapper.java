package com.oingmaryho.business.orderservice.presentation.dto.mapper;

import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailUpdateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderCreateRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderDetailCreateRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderDetailUpdateRequestDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderUpdateRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

@Mapper(componentModel = "spring")
public interface OrderPresentationMapper {
    OrdersRequestServiceDto toOrdersServiceDto(
        String productName,
        String recipientName,
        String requesterName,
        Boolean isDeleted,
        Pageable customPageable
    );

    @Mapping(target = "orderDetailId", source = "source.orderDetailId")
    @Mapping(target = "quantity", source = "source.quantity")
    @Mapping(target = "price", source = "source.price")
    OrderDetailUpdateResponseServiceDto toOrderDetailUpdateServiceDto(OrderDetailUpdateRequestDto source);

    OrderResponseServiceDto toOrderResponseServiceDto(OrderResponseServiceDto source);

    OrderRequestServiceDto toOrderServiceDto(UUID orderId);

    @Mapping(target = "requests", source = "source.requests")
    OrderUpdateServiceDto toOrderUpdateServiceDto(
        UUID id,
        OrderUpdateRequestDto source,
        List<OrderDetailUpdateResponseServiceDto> orderDetails
    );

    OrderDeleteServiceDto toOrderDeleteDto(UUID orderId);

    OrderDetailDeleteRequestServiceDto toOrderDetailDeleteRequestServiceDto(UUID orderId, UUID orderDetailId);

    OrderCreateRequestServiceDto toOrderCreateRequestServiceDto(OrderCreateRequestDto source, List<OrderDetailCreateRequestServiceDto> orderDetails);

    OrderDetailCreateRequestServiceDto toOrderDetailCreateRequestServiceDto(OrderDetailCreateRequestDto orderDetailCreateRequestDto);
}