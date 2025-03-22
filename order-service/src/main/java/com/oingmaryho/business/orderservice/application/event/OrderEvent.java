package com.oingmaryho.business.orderservice.application.event;

import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.Status;
import com.oingmaryho.business.orderservice.domain.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderEvent {
    private final OrderRepository orderRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void OrderSuccessEvent(Order order) {
        log.info("주문 성공 후 이벤트 실행");
        order.successOrder(Status.COMPLETE);
        orderRepository.save(order);
    }
}