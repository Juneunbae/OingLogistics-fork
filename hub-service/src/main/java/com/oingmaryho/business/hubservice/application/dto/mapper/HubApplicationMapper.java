package com.oingmaryho.business.hubservice.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oingmaryho.business.hubservice.application.dto.request.HubCreateRequestServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Hub;

@Mapper(componentModel = "spring")
public interface HubApplicationMapper {

	@Mapping(target = "address", source = "hub.address.address")
	@Mapping(target = "latitude", source = "hub.address.latitude")
	@Mapping(target = "longitude", source = "hub.address.longitude")
	HubSearchResponseServiceDto toHubSearchResponseServiceDto(Hub hub);

	@Mapping(target = "address", source = "hub.address.address")
	@Mapping(target = "latitude", source = "hub.address.latitude")
	@Mapping(target = "longitude", source = "hub.address.longitude")
	HubSearchAdminResponseServiceDto toHubSearchAdminResponseServiceDto(Hub hub);

	@Mapping(target = "address.address", source = "dto.address")
	@Mapping(target = "address.latitude", source = "dto.latitude")
	@Mapping(target = "address.longitude", source = "dto.longitude")
	@Mapping(target = "id", ignore = true)
	@Mapping(target = "isDeleted", ignore = true)
	Hub toHub(HubCreateRequestServiceDto dto);

	HubCreateResponseServiceDto toHubCreateResponseServiceDto(Hub hub);
}
