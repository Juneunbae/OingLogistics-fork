package com.oingmaryho.business.hubservice.presentation.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oingmaryho.business.hubservice.application.dto.request.HubSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.request.HubsSearchRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.presentation.dto.request.HubSearchRequestDto;
import com.oingmaryho.business.hubservice.presentation.dto.response.HubSearchResponseDto;

@Mapper(componentModel = "spring")
public interface HubPresentationMapper {

	HubSearchRequestServiceDto toHubSearchRequestServiceDto(UUID id);

	HubSearchResponseDto toHubSearchResponseDto(HubSearchResponseServiceDto dto);

	HubsSearchRequestServiceDto toHubsSearchRequestServiceDto(HubSearchRequestDto dto);
}
