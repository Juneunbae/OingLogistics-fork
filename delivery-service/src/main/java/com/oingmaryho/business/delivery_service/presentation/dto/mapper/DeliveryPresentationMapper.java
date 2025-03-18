package com.oingmaryho.business.delivery_service.presentation.dto.mapper;

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

    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "customPageable", source = "customPageable")
    DeliverySearchRequestServiceDto toSearchServiceDto(Long userId, DeliverySearchRequestDto requestDto, Pageable customPageable);

    DeliveryRouteDetailRequestServiceDto toRouteDetailServiceDto(UUID id);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "userId", source = "userId")
    @Mapping(target = "customPageable", source = "customPageable")
    DeliveryRouteSearchRequestServiceDto toRouteSearchServiceDto(UUID id, Long userId, DeliveryRouteSearchRequestDto searchDto, Pageable customPageable);

    // ResponseServiceDto -> ResponseDto
    DeliveryCreationResponseDto toCreationResponseDto(DeliveryCreationResponseServiceDto responseServiceDto);

    DeliveryUpdateResponseDto toUpdateResponseDto(DeliveryUpdateResponseServiceDto responseServiceDto);

    DeliveryUpdateStatusResponseDto toUpdateStatusResponseDto(DeliveryUpdateStatusResponseServiceDto responseServiceDto);

    // 배송 조회
    DeliveryResponseDto toDetailResponseDto(DeliveryResponseServiceDto responseServiceDto);

    // 배송 검색
    DeliveryResponseDto toSearchResponseDto(DeliveryResponseServiceDto responseServiceDto);

    // 배송 경로 조회
    DeliveryRouteResponseDto toRouteDetailResponseDto(DeliveryRouteResponseServiceDto responseServiceDto);

    // 배송 경로 검색
    DeliveryRouteResponseDto toRouteSearchResponseDto(DeliveryRouteResponseServiceDto responseServiceDto);

}
