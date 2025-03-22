package com.oingmaryho.business.hubservice.presentation.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.hubservice.application.dto.request.HubPathRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteSearchAdminRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRouteUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubRoutesSearchAdminRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubPathRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteSearchAdminRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubRouteUpdateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteCreateResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteSearchAdminResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteSearchResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubRouteUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface HubRoutePresentationMapper {

	HubRouteCreateRequestServiceDto toHubRouteCreateRequestServiceDto(HubRouteCreateRequestDto dto);

	HubRouteCreateResponseDto toHubRouteCreateResponseDto(HubRouteCreateResponseServiceDto dto);

	HubRouteUpdateRequestServiceDto toHubRouteUpdateRequestServiceDto(HubRouteUpdateRequestDto dto);

	HubRouteUpdateResponseDto toHubRouteUpdateResponseDto(HubRouteUpdateResponseServiceDto dto);

	HubRouteDeleteRequestServiceDto toHubRouteDeleteRequestServiceDto(UUID id);

	HubRouteSearchAdminRequestServiceDto toHubRouteSearchAdminRequestServiceDto(UUID id);

	HubRoutesSearchAdminRequestServiceDto toHubRoutesSearchAdminRequestServiceDto(HubRouteSearchAdminRequestDto dto);

	HubRouteSearchAdminResponseDto toHubRouteSearchAdminResponseDto(HubRouteSearchAdminResponseServiceDto dto);

	HubPathRequestServiceDto toHubPathRequestServiceDto(HubPathRequestDto dto);

	HubRouteSearchResponseDto toHubRouteSearchResponseDto(HubRouteSearchResponseServiceDto dto);
}
