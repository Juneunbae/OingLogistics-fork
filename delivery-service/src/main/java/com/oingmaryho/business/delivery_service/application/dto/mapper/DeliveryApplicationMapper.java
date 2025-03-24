package com.oingmaryho.business.delivery_service.application.dto.mapper;

import com.oingmaryho.business.delivery_service.application.dto.request.DeliveryCreationRequestServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.*;
import com.oingmaryho.business.delivery_service.domain.entity.Delivery;
import com.oingmaryho.business.delivery_service.domain.entity.DeliveryRoute;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.UUID;


@Mapper(componentModel = "spring")
public interface DeliveryApplicationMapper {

    // RequestServiceDto -> Entity
    @BeanMapping(ignoreByDefault = true)
    Delivery toDelivery(UUID managerId,
                        UUID departureHubId,
                        UUID destinationHubId,
                        DeliveryCreationRequestServiceDto requestServiceDto,
                        List<DeliveryRoute> routes);

    // Entity -> ResponseServiceDto
    DeliveryCreationResponseServiceDto toCreationResponseServiceDto(UUID orderId, UUID orderDetailId, UUID deliveryId);
    DeliveryUpdateResponseServiceDto toUpdateResponseServiceDto(UUID id);
    DeliveryUpdateStatusResponseServiceDto toUpdateStatusResponseServiceDto(UUID id);
    DeliveryRouteUpdateStatusResponseServiceDto toUpdateRouteStatusResponseServiceDto(UUID id);
    // 배송 정보
    @Mapping(target = "managerId", source = "delivery.manager.id")
    DeliveryResponseServiceDto toDeliveryResponseServiceDto(Delivery delivery);

    // 배송 경로 정보
    @Mapping(target = "deliveryId", source = "route.delivery.id")
    @Mapping(target = "managerId", source = "delivery.manager.id")
    DeliveryRouteResponseServiceDto toRouteResponseServiceDto(DeliveryRoute route);



}
