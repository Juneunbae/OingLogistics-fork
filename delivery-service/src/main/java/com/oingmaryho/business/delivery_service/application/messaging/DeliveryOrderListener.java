package com.oingmaryho.business.delivery_service.application.messaging;

import com.oingmaryho.business.delivery_service.application.dto.request.DeliveryCreationRequestServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryCreationResponseServiceDto;
import com.oingmaryho.business.delivery_service.application.service.DeliveryAdminService;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryCreationRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.response.DeliveryCreationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryOrderListener {

    private final RabbitTemplate rabbitTemplate;
    private final DeliveryAdminService deliveryAdminService;

    @Value("${message.queue.order}")
    private String queueOrder;

    @RabbitListener(queues = "queueDelivery")
    @Transactional
    public void createDelivery(DeliveryCreationRequestDto requestDto) {
        log.info("[Delivery Creation Request] orderId = {}, orderDetailId = {}",requestDto.orderId(), requestDto.orderDetailId());

        DeliveryCreationRequestServiceDto requestServiceDto = new DeliveryCreationRequestServiceDto(
                requestDto.orderId(),
                requestDto.orderDetailId(),
                requestDto.requesterAddress(),
                requestDto.requesterName(),
                requestDto.requesterSlackId(),
                requestDto.recipientHubId()
        );

        DeliveryCreationResponseServiceDto responseServiceDto = deliveryAdminService.createDelivery(requestServiceDto);

        UUID deliveryId = UUID.randomUUID();
        log.info("[Delivery Creation Success Response] orderId = {}, orderDetailId = {}, deliveryId = {}",requestDto.orderId(), requestDto.orderDetailId(), deliveryId);

        rabbitTemplate.convertAndSend(queueOrder, new DeliveryCreationResponseDto(
                requestDto.orderId(),
                requestDto.orderDetailId(),
                deliveryId));
//                responseServiceDto.id()));
    }

}
