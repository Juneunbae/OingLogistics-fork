package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.presentation.dto.request.*;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DeliveryPresentationMapper {
    DeliveryPresentationMapper INSTANCE = Mappers.getMapper(DeliveryPresentationMapper.class);

    DeliveryUpdateRequestServiceDto toUpdateServiceDto(UUID id, DeliveryUpdateRequestDto requestDto);

    DeliveryUpdateStatusRequestServiceDto toUpdateStatusServiceDto(UUID id, DeliveryUpdateStatusRequestDto requestDto);

    DeliveryDeletionRequestServiceDto toDeletionServiceDto(UUID id);

    DeliveryDetailRequestServiceDto toDetailServiceDto(UUID id);

    DeliverySearchRequestServiceDto toSearchServiceDto(DeliverySearchRequestDto searchDto);

    DeliveryRouteDetailRequestServiceDto toRouteDetailServiceDto(UUID id);

    DeliveryRouteSearchRequestServiceDto toRouteSearchServiceDto(UUID id, DeliveryRouteSearchRequestDto searchDto);
}
