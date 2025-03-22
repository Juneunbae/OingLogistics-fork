package com.oingmaryho.business.hubservice.presentation.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.hubservice.application.dto.request.HubCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubDeleteRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubPathRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubsSearchAdminRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubsSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubPathRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubSearchAdminRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubSearchRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubUpdateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubCreateResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubSearchAdminResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubSearchResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface HubPresentationMapper {

	HubSearchRequestServiceDto toHubSearchRequestServiceDto(UUID id);

	HubSearchResponseDto toHubSearchResponseDto(HubSearchResponseServiceDto dto);

	HubSearchAdminResponseDto toHubSearchAdminResponseDto(HubSearchAdminResponseServiceDto dto);

	HubsSearchRequestServiceDto toHubsSearchRequestServiceDto(HubSearchRequestDto dto);

	HubsSearchAdminRequestServiceDto toHubsSearchAdminRequestServiceDto(HubSearchAdminRequestDto dto);

	HubCreateRequestServiceDto toHubCreateRequestServiceDto(HubCreateRequestDto dto);

	HubCreateResponseDto toHubCreateResponseDto(HubCreateResponseServiceDto dto);

	HubUpdateRequestServiceDto toHubUpdateRequestServiceDto(HubUpdateRequestDto dto);

	HubUpdateResponseDto toHubUpdateResponseDto(HubUpdateResponseServiceDto dto);

	HubDeleteRequestServiceDto toHubDeleteRequestServiceDto(UUID id);

	HubPathRequestServiceDto toHubPathRequestServiceDto(HubPathRequestDto dto);
}
