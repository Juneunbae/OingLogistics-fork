package com.oingmaryho.business.delivery_service.application.event;

import com.oingmaryho.business.delivery_service.application.dto.request.DeliveryCreationRequestServiceDto;
import com.oingmaryho.business.delivery_service.application.dto.response.DeliveryCreationResponseServiceDto;
import com.oingmaryho.business.delivery_service.application.service.DeliveryAdminService;
import com.oingmaryho.business.delivery_service.presentation.dto.request.DeliveryCreationRequestDto;
import com.oingmaryho.business.delivery_service.presentation.dto.response.DeliveryCreationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;
import static org.springframework.transaction.event.TransactionPhase.AFTER_COMMIT;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeliveryCreationEventHandler {

    private final RabbitTemplate rabbitTemplate;
    private final DeliveryAdminService deliveryAdminService;

    @Value("${message.queue.order}")
    private String queueOrder;

    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener(phase = AFTER_COMMIT)
    public void createDelivery(DeliveryCreationRequestDto requestDto) {

        log.info("[Delivery Creation Request] orderId = {}, orderDetailId = {}",
                requestDto.orderId(), requestDto.orderDetailId());

        DeliveryCreationRequestServiceDto requestServiceDto = new DeliveryCreationRequestServiceDto(
                requestDto.orderId(),
                requestDto.orderDetailId(),
                requestDto.recipientId(),
                requestDto.requesterAddress(),
                requestDto.requesterName(),
                requestDto.requesterSlackId(),
                requestDto.recipientHubId()
        );

        DeliveryCreationResponseServiceDto responseServiceDto = deliveryAdminService.createDelivery(requestServiceDto);

        log.info("[Delivery Creation Success] orderId = {}, orderDetailId = {}, deliveryId = {}",
                responseServiceDto.orderId(),responseServiceDto.orderDetailId(), responseServiceDto.deliveryId());

        try {
            rabbitTemplate.convertAndSend(queueOrder, new DeliveryCreationResponseDto(
                    requestDto.orderId(),
                    requestDto.orderDetailId(),
                    responseServiceDto.deliveryId()));
            log.info("[Delivery Creation Success Message Issued] orderId = {}, orderDetailId = {}, deliveryId = {}",
                    responseServiceDto.orderId(),responseServiceDto.orderDetailId(), responseServiceDto.deliveryId());
        } catch (AmqpException e) {
            // 배송 생성 성공하고 DB에 반영이 되었지만, 전송 단계에서 에러가 발생했을 경우
            e.fillInStackTrace();
            log.info("[Delivery Creation Success Message NOT Issued] orderId = {}, orderDetailId = {}, deliveryId = {}",
                    responseServiceDto.orderId(),responseServiceDto.orderDetailId(), responseServiceDto.deliveryId());
        }

    }

}
