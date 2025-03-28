package com.oingmaryho.business.orderservice.presentation.dto.mapper;

import com.oingmaryho.business.orderservice.application.dto.request.*;
import com.oingmaryho.business.orderservice.application.dto.response.OrderCreateResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderDetailUpdateServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderResponseServiceDto;
import com.oingmaryho.business.orderservice.application.dto.response.OrderUpdateResponseServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.*;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderAdminResponseServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderCreateResponseDto;
import com.oingmaryho.business.orderservice.presentation.dto.response.OrderUpdateResponseDto;
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
    OrderDetailUpdateServiceDto toOrderDetailUpdateServiceDto(OrderDetailUpdateRequestDto source);

    OrderResponseServiceDto toOrderResponseServiceDto(OrderResponseServiceDto source);

    OrderRequestServiceDto toOrderServiceDto(UUID orderId);

    @Mapping(target = "requests", source = "source.requests")
    OrderUpdateServiceDto toOrderUpdateServiceDto(
        UUID id,
        OrderUpdateRequestDto source,
        List<OrderDetailUpdateServiceDto> orderDetails
    );

    OrderDeleteServiceDto toOrderDeleteDto(UUID orderId);

    OrderDetailDeleteRequestServiceDto toOrderDetailDeleteRequestServiceDto(UUID orderId, UUID orderDetailId);

    OrderCreateRequestServiceDto toOrderCreateRequestServiceDto(
        Long userId,
        String username,
        String slackId,
        OrderCreateRequestDto source,
        List<OrderDetailCreateRequestServiceDto> orderDetails
    );

    OrderDetailCreateRequestServiceDto toOrderDetailCreateRequestServiceDto(OrderDetailCreateRequestDto orderDetailCreateRequestDto);

    OrderAdminRequestServiceDto toOrderAdminRequestServiceDto(
        String productName, String recipientName, String requesterName, Boolean isDeleted, Pageable customPageable
    );

    OrderAdminResponseServiceDto toOrderAdminResponseServiceDto(OrderAdminResponseServiceDto source);

    OrderCreateResponseDto toOrderCreateResponseDto(OrderCreateResponseServiceDto source);

    OrderUpdateResponseDto toOrderUpdateResponseDto(OrderUpdateResponseServiceDto source);
}