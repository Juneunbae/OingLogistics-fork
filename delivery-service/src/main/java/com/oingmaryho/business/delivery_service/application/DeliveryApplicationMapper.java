package com.oingmaryho.business.delivery_service.application;

import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DeliveryApplicationMapper {
    DeliveryApplicationMapper INSTANCE = Mappers.getMapper(DeliveryApplicationMapper.class);

    DeliveryCreationResponseDto toCreationResponseDto(DeliveryCreationResponseServiceDto responseServiceDto);

    DeliveryUpdateResponseDto toUpdateResponseDto(DeliveryUpdateResponseServiceDto responseServiceDto);

    DeliveryUpdateStatusResponseDto toUpdateStatusResponseDto(DeliveryUpdateStatusResponseServiceDto responseServiceDto);

    DeliveryDetailResponseDto toDetailResponseDto(DeliveryDetailResponseServiceDto responseServiceDto);

    DeliveryResponseDto toSearchResponseDto(DeliveryResponseServiceDto responseServiceDto);

    DeliveryRouteDetailResponseDto toRouteDetailResponseDto(DeliveryRouteDetailResponseServiceDto responseServiceDto);

    DeliveryRouteResponseDto toRouteSearchResponseDto(DeliveryRouteResponseServiceDto responseServiceDto);
}
