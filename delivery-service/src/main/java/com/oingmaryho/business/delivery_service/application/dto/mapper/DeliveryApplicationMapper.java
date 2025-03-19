package com.oingmaryho.business.delivery_service.application.dto.mapper;

import com.oingmaryho.business.delivery_service.application.dto.request.DeliveryCreationRequestServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryResponseServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryRouteResponseServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryUpdateResponseServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryUpdateStatusResponseServiceDto;
import com.oingmaryho.business.delivery_service.domain.Delivery;
import com.oingmaryho.business.delivery_service.domain.DeliveryRoute;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;


@Mapper(componentModel = "spring")
public interface DeliveryApplicationMapper {
    DeliveryApplicationMapper INSTANCE = Mappers.getMapper(DeliveryApplicationMapper.class);

    // RequestServiceDto -> Entity
    @BeanMapping(ignoreByDefault = true)
    Delivery toDelivery(UUID managerId,
                        UUID departureHubId,
                        UUID destinationHubId,
                        DeliveryCreationRequestServiceDto requestServiceDto,
                        List<DeliveryRoute> routes);

    // TODO toDeliveryRoute

    // Entity -> ResponseServiceDto
    DeliveryUpdateResponseServiceDto toUpdateResponseServiceDto(UUID id);
    DeliveryUpdateStatusResponseServiceDto toUpdateStatusResponseServiceDto(UUID id);
    // 배송 정보
    DeliveryResponseServiceDto toDeliveryResponseServiceDto(Delivery delivery);

    // 배송 경로 정보
    @Mapping(target = "deliveryId", source = "route.delivery.id")
    DeliveryRouteResponseServiceDto toRouteResponseServiceDto(DeliveryRoute route);


}
