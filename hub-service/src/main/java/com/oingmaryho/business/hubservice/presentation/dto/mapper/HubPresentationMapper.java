package com.oingmaryho.business.hubservice.presentation.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.hubservice.application.dto.request.HubCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubUpdateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubsSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubUpdateResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubCreateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubSearchRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubUpdateRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubCreateResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubSearchResponseDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface HubPresentationMapper {

	HubSearchRequestServiceDto toHubSearchRequestServiceDto(UUID id);

	HubSearchResponseDto toHubSearchResponseDto(HubSearchResponseServiceDto dto);

	HubsSearchRequestServiceDto toHubsSearchRequestServiceDto(HubSearchRequestDto dto);

	HubCreateRequestServiceDto toHubCreateRequestServiceDto(HubCreateRequestDto dto);

	HubCreateResponseDto toHubCreateResponseDto(HubCreateResponseServiceDto dto);

	HubUpdateRequestServiceDto toHubUpdateRequestServiceDto(HubUpdateRequestDto dto);

	HubUpdateResponseDto toHubUpdateResponseDto(HubUpdateResponseServiceDto dto);
}
