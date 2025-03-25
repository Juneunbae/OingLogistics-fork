package com.oingmaryho.business.orderservice.application.service.queue;

import com.oingmaryho.business.orderservice.application.dto.request.SlackMessageDto;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderQueue {
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;
    private final OrderJPARepository orderJPARepository;

    @Value("${message.queue.slack}")
    private String queueSlack;

    @RabbitListener(queues = "${message.queue.order}")
    @Transactional
    public void processQueueOrder(DeliveryCreationResponseDto deliveryCreationResponseDto) {
        log.info("receive orderQueue: {}", deliveryCreationResponseDto);

        Order order = getOrderById(deliveryCreationResponseDto.orderId());
        OrderDetail orderDetail = getByOrderDetailId(order, deliveryCreationResponseDto.orderDetailId());
        orderDetail.updateDelivery(deliveryCreationResponseDto.deliveryId());
        orderRepository.save(order);

        String[] stopoverNames = deliveryCreationResponseDto.deliveryStopoverNames().split(",");

        String message = makeMessage(order, orderDetail, deliveryCreationResponseDto, stopoverNames);
        log.info("메시지 생성 성공");

        rabbitTemplate.convertAndSend(queueSlack, new SlackMessageDto(order.getRequesterUserId(), message));
        log.info("queueSlack 전송 성공");
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

    private String makeMessage(Order order, OrderDetail orderDetail, DeliveryCreationResponseDto deliveryCreationResponseDto, String[] stopoverNames) {
        StringBuilder messageBuilder = new StringBuilder();

        messageBuilder.append(String.format("주문 번호: %s, \n", order.getId()));
        messageBuilder.append(String.format("주문자 정보: %s / %s \n", order.getRequesterUsername(), order.getRequesterSlackId()));
        messageBuilder.append(String.format("상품 정보: %s \n", orderDetail.getProductName()));
        messageBuilder.append(String.format("요청 사항: %s \n", order.getRequests()));
        messageBuilder.append(String.format("발송지: %s \n", deliveryCreationResponseDto.deliveryDepartureName()));

        // 경유지 처리
        if (stopoverNames != null && stopoverNames.length > 0) {
            messageBuilder.append("경유지: ");
            for (String stopoverName : stopoverNames) {
                messageBuilder.append(stopoverName).append(", ");
            }
            messageBuilder.delete(messageBuilder.length() - 2, messageBuilder.length());
            messageBuilder.append("\n");
        } else {
            messageBuilder.append("경유지: 없음\n");
        }

        messageBuilder.append(String.format("도착지: %s \n", deliveryCreationResponseDto.deliveryDestinationName()));
        messageBuilder.append(String.format("배송담당자: %s / %s", deliveryCreationResponseDto.deliveryManagerName(), deliveryCreationResponseDto.deliveryManagerSlackId()));

        return messageBuilder.toString();
    }
}