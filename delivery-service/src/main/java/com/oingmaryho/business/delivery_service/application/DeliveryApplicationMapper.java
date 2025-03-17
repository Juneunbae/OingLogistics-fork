package com.oingmaryho.business.delivery_service.application;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DeliveryApplicationMapper {
    DeliveryApplicationMapper INSTANCE = Mappers.getMapper(DeliveryApplicationMapper.class);

    // RequestServiceDto -> Entity


    // Entity -> RequestServiceDto


}
