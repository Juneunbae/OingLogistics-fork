package com.oingmaryho.business.orderservice.infrastructure;


import com.oingmaryho.business.orderservice.domain.Order;
import com.oingmaryho.business.orderservice.domain.OrderSearchCriteria;
import com.oingmaryho.business.orderservice.domain.repository.CustomOrderRepository;
import com.oingmaryho.business.orderservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import static com.oingmaryho.business.orderservice.domain.QOrder.order;
import static com.oingmaryho.business.orderservice.domain.QOrderDetail.orderDetail;

@Repository
@RequiredArgsConstructor
public class OrderQueryRepository implements CustomOrderRepository {
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Order> findDynamicQuery(OrderSearchCriteria criteria, Pageable pageable) {
        BooleanBuilder conditions = new BooleanBuilder(); // 조건을 동적으로 추가할 수 있음

        if (criteria.getIsDeleted() != null) {
            conditions.and(order.isDeleted.eq(criteria.getIsDeleted()));
        }

        if (criteria.getProductName() != null && !criteria.getProductName().isEmpty()) {
            conditions.and(orderDetail.productName.likeIgnoreCase("%" + criteria.getProductName() + "%"));
        } else {
            // productName이 없을 경우에는 해당 조건을 무시하도록 처리
            conditions.and(orderDetail.productName.isNull().or(orderDetail.productName.isNotNull()));
        }

        if (criteria.getRecipientName() != null && !criteria.getRecipientName().isEmpty()) {
            conditions.and(orderDetail.recipientName.likeIgnoreCase("%" + criteria.getRecipientName() + "%"));
        } else {
            // recipientName이 없을 경우에는 해당 조건을 무시하도록 처리
            conditions.and(orderDetail.recipientName.isNull().or(orderDetail.recipientName.isNotNull()));
        }

        if (criteria.getRequesterName() != null && !criteria.getRequesterName().isEmpty()) {
            conditions.and(order.requesterName.likeIgnoreCase("%" + criteria.getRequesterName() + "%"));
        }

        JPAQuery<Order> query = queryFactory
            .selectFrom(order)
            .leftJoin(orderDetail).on(orderDetail.order.eq(order)).fetchJoin()
            .where(conditions)
            .orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Order.class))
            .offset(pageable.getOffset())
            .limit(pageable.getPageSize());

        Long total = queryFactory
            .select(order.id.count())
            .from(order)
            .leftJoin(order.orderDetails, orderDetail)
            .where(conditions)
            .fetchOne();

        assert total != null;

        return new PageImpl<>(query.fetch(), pageable, total);
    }
}