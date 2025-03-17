package com.oingmaryho.business.delivery_service.presentation;

import com.oingmaryho.business.delivery_service.application.dto.request.*;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.presentation.dto.request.*;
import com.oingmaryho.business.delivery_service.presentation.dto.response.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface DeliveryPresentationMapper {
    DeliveryPresentationMapper INSTANCE = Mappers.getMapper(DeliveryPresentationMapper.class);

    // RequestServiceDto -> RequestDto
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


    // ResponseServiceDto -> ResponseDto
    DeliveryCreationResponseDto toCreationResponseDto(DeliveryCreationResponseServiceDto responseServiceDto);

    DeliveryUpdateResponseDto toUpdateResponseDto(DeliveryUpdateResponseServiceDto responseServiceDto);

    DeliveryUpdateStatusResponseDto toUpdateStatusResponseDto(DeliveryUpdateStatusResponseServiceDto responseServiceDto);

    DeliveryDetailResponseDto toDetailResponseDto(DeliveryDetailResponseServiceDto responseServiceDto);

    @Mapping(target = "page", source = "customPageable.page")
    @Mapping(target = "size", source = "customPageable.size")
    @Mapping(target = "sortDirection", source = "customPageable.sortDirection")
    DeliveryResponseDto toSearchResponseDto(DeliveryResponseServiceDto responseServiceDto);

    DeliveryRouteDetailResponseDto toRouteDetailResponseDto(DeliveryRouteDetailResponseServiceDto responseServiceDto);

    @Mapping(target = "page", source = "customPageable.page")
    @Mapping(target = "size", source = "customPageable.size")
    @Mapping(target = "sortDirection", source = "customPageable.sortDirection")
    DeliveryRouteResponseDto toRouteSearchResponseDto(DeliveryRouteResponseServiceDto responseServiceDto);
}
