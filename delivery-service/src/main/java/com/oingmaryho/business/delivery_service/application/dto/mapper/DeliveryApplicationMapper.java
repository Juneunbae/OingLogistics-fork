package com.oingmaryho.business.delivery_service.application.dto.mapper;

import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryResponseServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryRouteResponseServiceDto;
import com.oingmaryho.business.delivery_service.domain.Delivery;
import com.oingmaryho.business.delivery_service.domain.DeliveryRoute;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;


@Mapper(componentModel = "spring")
public interface DeliveryApplicationMapper {
    DeliveryApplicationMapper INSTANCE = Mappers.getMapper(DeliveryApplicationMapper.class);

    // RequestServiceDto -> Entity

    // Entity -> ResponseServiceDto
    // 배송 정보
    DeliveryResponseServiceDto toDeliveryResponseServiceDto(Delivery delivery);

    // 배송 경로 정보
    @Mapping(target = "deliveryId", source = "route.delivery.id")
    DeliveryRouteResponseServiceDto toRouteResponseServiceDto(DeliveryRoute route);

}
