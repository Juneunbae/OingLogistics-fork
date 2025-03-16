package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.presentation.dto.request.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DeliveryPresentationMapper {
    DeliveryPresentationMapper INSTANCE = Mappers.getMapper(DeliveryPresentationMapper.class);

    DeliveryCreationRequestServiceDto toCreationServiceDto(DeliveryCreationRequestDto requestDto);

    @Mapping(target = "id", source = "id")
    DeliveryUpdateRequestServiceDto toUpdateServiceDto(UUID id, DeliveryUpdateRequestDto requestDto);

    @Mapping(target = "id", source = "id")
    DeliveryUpdateStatusRequestServiceDto toUpdateStatusServiceDto(UUID id, DeliveryUpdateStatusRequestDto requestDto);

    DeliveryDeletionRequestServiceDto toDeletionServiceDto(UUID id);

    DeliveryDetailRequestServiceDto toDetailServiceDto(UUID id);

    @Mapping(target = "customPageable", source = "customPageable")
    DeliverySearchRequestServiceDto toSearchServiceDto(DeliverySearchRequestDto searchDto, Pageable customPageable);

    DeliveryRouteDetailRequestServiceDto toRouteDetailServiceDto(UUID id);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "customPageable", source = "customPageable")
    DeliveryRouteSearchRequestServiceDto toRouteSearchServiceDto(UUID id, DeliveryRouteSearchRequestDto searchDto, Pageable customPageable);

}
