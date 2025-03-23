package com.oingmaryho.business.orderservice.application.service.queue;

import com.oingmaryho.business.orderservice.application.dto.response.DeliveryCreationResponseDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import com.oingmaryho.business.orderservice.exception.ErrorCode;
import com.oingmaryho.business.orderservice.exception.OrderException;
import com.oingmaryho.business.orderservice.infrastructure.OrderJPARepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderQueue {
    private final OrderRepository orderRepository;
    private final OrderJPARepository orderJPARepository;

    @RabbitListener(queues = "${message.queue.order}")
    @Transactional
    public void processQueueOrder(DeliveryCreationResponseDto deliveryCreationResponseDto) throws IOException {
        log.info("receive orderQueue: {}", deliveryCreationResponseDto);

        Order order = getOrderById(deliveryCreationResponseDto.orderId());

        OrderDetail orderDetail = getByOrderDetailId(order, deliveryCreationResponseDto.orderDetailId());

        orderDetail.updateDelivery(deliveryCreationResponseDto.deliveryId());
        orderRepository.save(order);
    }

    private Order getOrderById(UUID orderId) {
        return orderJPARepository.findById(orderId)
            .orElseThrow(() -> new OrderException(ErrorCode.NOT_FOUND));
    }

    private OrderDetail getByOrderDetailId(Order order, UUID orderDetailId) {
        return order.getOrderDetails().stream().filter(
            orderDetail -> orderDetail.getId().equals(orderDetailId) && !orderDetail.getIsDeleted()
        ).findFirst().orElseThrow(() -> new OrderException(ErrorCode.ORDER_DETAIL_NOT_FOUND));
    }
}