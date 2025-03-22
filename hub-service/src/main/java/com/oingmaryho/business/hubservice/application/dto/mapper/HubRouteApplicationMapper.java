package com.oingmaryho.business.hubservice.application.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oingmaryho.business.hubservice.application.dto.response.HubRouteCreateResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchAdminResponseServiceDto;
import com.oingmaryho.business.hubservice.application.dto.response.HubRouteSearchResponseServiceDto;
import com.oingmaryho.business.hubservice.domain.HubRoute;

@Mapper(componentModel = "spring")
public interface HubRouteApplicationMapper {

	HubRouteCreateResponseServiceDto toHubRouteCreateResponseServiceDto(HubRoute hubRoute);

	@Mapping(target = "hubToHubTime", source = "hubRoute.routeInfo.hubToHubTime")
	@Mapping(target = "distance", source = "hubRoute.routeInfo.distance")
	HubRouteSearchAdminResponseServiceDto toHubRouteSearchAdminResponseServiceDto(HubRoute hubRoute);

	@Mapping(target = "hubToHubTime", source = "hubRoute.routeInfo.hubToHubTime")
	@Mapping(target = "distance", source = "hubRoute.routeInfo.distance")
	HubRouteSearchResponseServiceDto toHubRouteSearchResponseServiceDto(HubRoute hubRoute);
}
