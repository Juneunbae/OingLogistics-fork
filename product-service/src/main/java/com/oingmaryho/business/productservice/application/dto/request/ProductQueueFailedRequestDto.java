package com.oingmaryho.business.productservice.application.dto.request;

import java.util.UUID;

public record ProductQueueFailedRequestDto(
	UUID productId,
	Integer quantity
) {
}
