package com.oingmaryho.business.orderservice.application.event;

import com.oingmaryho.business.orderservice.application.dto.mapper.OrderApplicationMapper;
import com.oingmaryho.business.orderservice.application.dto.request.DeliveryCreationRequestDto;
import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderDetail;
import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEventHandler {
    private final RabbitTemplate rabbitTemplate;
    private final OrderRepository orderRepository;
    private final OrderApplicationMapper orderApplicationMapper;

    @Value("${message.queue.delivery}")
    private String queueDelivery;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void OrderSuccessEvent(OrderEvent orderEvent) {
        log.info("주문 성공 후 이벤트 실행");

        Order order = orderEvent.order();

        order.successOrder(Status.COMPLETE);
        orderRepository.save(order);
        log.info("주문: {},  상태 변경 완료", order.getId());

        for (OrderDetail orderDetail : order.getOrderDetails()) {
            DeliveryCreationRequestDto deliveryCreationRequestDto = orderApplicationMapper.toDeliveryCreationRequestDto(
                order, orderDetail
            );

            log.info("DeliveryCreationRequestDto: {}", deliveryCreationRequestDto);
            rabbitTemplate.convertAndSend(queueDelivery, deliveryCreationRequestDto);
        }

        log.info("queueDelivery, 메시지 큐 전달 완료");
    }
}