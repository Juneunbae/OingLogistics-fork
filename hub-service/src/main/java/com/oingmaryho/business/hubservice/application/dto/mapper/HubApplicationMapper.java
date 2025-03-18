package com.oingmaryho.business.hubservice.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oingmaryho.business.hubservice.application.dto.response.HubSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.Hub;

@Mapper(componentModel = "spring")
public interface HubApplicationMapper {

	@Mapping(target = "address", source = "hub.address.address")
	@Mapping(target = "latitude", source = "hub.address.latitude")
	@Mapping(target = "longitude", source = "hub.address.longitude")
	HubSearchResponseServiceDto toHubSearchResponseServiceDto(Hub hub);
}
