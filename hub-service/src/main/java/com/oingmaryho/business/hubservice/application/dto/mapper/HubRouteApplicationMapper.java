package com.oingmaryho.business.hubservice.application.dto.mapper;

import org.mapstruct.Mapper;

import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.HubRoute;

@Mapper(componentModel = "spring")
public interface HubRouteApplicationMapper {

	HubRouteCreateResponseServiceDto toHubRouteCreateResponseServiceDto(HubRoute hubRoute);
}
