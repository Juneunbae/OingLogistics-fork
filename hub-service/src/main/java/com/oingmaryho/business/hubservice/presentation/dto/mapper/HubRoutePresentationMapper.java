package com.oingmaryho.business.hubservice.presentation.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.hubservice.application.dto.request.HubRouteCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteUpdateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteCreateResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface HubRoutePresentationMapper {

	HubRouteCreateRequestServiceDto toHubRouteCreateRequestServiceDto(HubRouteCreateRequestDto dto);

	HubRouteCreateResponseDto toHubRouteCreateResponseDto(HubRouteCreateResponseServiceDto dto);

	HubRouteUpdateRequestServiceDto toHubRouteUpdateRequestServiceDto(HubRouteUpdateRequestDto dto);

	HubRouteUpdateResponseDto toHubRouteUpdateResponseDto(HubRouteUpdateResponseServiceDto dto);

	HubRouteDeleteRequestServiceDto toHubRouteDeleteRequestServiceDto(UUID id);
}
