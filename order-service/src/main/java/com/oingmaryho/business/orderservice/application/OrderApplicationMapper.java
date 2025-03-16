package com.oingmaryho.business.orderservice.application;

import com.oingmaryho.business.orderservice.application.dto.OrderServiceDto;
import com.oingmaryho.business.orderservice.application.dto.OrdersServiceDto;
import com.oingmaryho.business.orderservice.presentation.dto.request.OrderSearchRequestDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Pageable;

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
}