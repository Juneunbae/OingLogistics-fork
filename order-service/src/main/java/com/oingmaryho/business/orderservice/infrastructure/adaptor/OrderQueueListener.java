package com.oingmaryho.business.orderservice.infrastructure.adaptor;

import com.oingmaryho.business.orderservice.application.dto.response.DeliveryCreationResponseDto;
import com.oingmaryho.business.orderservice.application.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class OrderQueueListener {
    private final OrderService orderService;

    @Transactional
    @RabbitListener(queues = "${message.queue.order}")
    public void queueOrderListen(DeliveryCreationResponseDto deliveryCreationResponseDto) {
        orderService.processOrderQueue(deliveryCreationResponseDto);
    }
}