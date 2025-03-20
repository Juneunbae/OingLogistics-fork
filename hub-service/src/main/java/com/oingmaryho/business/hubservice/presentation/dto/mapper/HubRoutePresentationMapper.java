package com.oingmaryho.business.hubservice.presentation.dto.mapper;

import org.mapstruct.Mapper;

import com.oingmaryho.business.hubservice.application.dto.request.HubRouteCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteCreateResponseDto;

@Mapper(componentModel = "spring")
public interface HubRoutePresentationMapper {

	HubRouteCreateRequestServiceDto toHubRouteCreateRequestServiceDto(HubRouteCreateRequestDto dto);

	HubRouteCreateResponseDto toHubRouteCreateResponseDto(HubRouteCreateResponseServiceDto dto);
}
