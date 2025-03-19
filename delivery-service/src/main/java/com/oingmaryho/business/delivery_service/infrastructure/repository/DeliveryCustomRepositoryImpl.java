package com.oingmaryho.business.delivery_service.infrastructure.repository;

import com.oingmaryho.business.delivery_service.utils.QueryDslUtils;
import com.oingmaryho.business.delivery_service.domain.*;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class DeliveryCustomRepositoryImpl implements DeliveryCustomRepository {
    private final JPAQueryFactory queryFactory;

    QDelivery qDelivery = QDelivery.delivery;
    QDeliveryRoute qDeliveryRoute = QDeliveryRoute.deliveryRoute;
    QDeliveryManager qDeliveryManager = QDeliveryManager.deliveryManager;

    /**
     * 배송 검색
     * @param hubId 허브 id
     * @param companyId 업체 id
     * @param managerId 배송 담당자 id
     * @param managerType   배송 담당자 타입
     * @param pageable customPageable
     * @return 배송
     */
    @Override
    public Page<Delivery> searchDelivery(UUID hubId,
                                         UUID companyId,
                                         UUID managerId,
                                         DeliveryManagerType managerType,
                                         Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (hubId != null) {        // 허브 관리자가 허브 id로 조회
            builder.and(qDelivery.departureHubId.eq(hubId));
        }

        if (companyId != null) {    // 업체 관리자가 업체 id로 조회
            builder.and(qDelivery.manager.companyId.eq(companyId));
        }

        if (managerId != null && managerType != null) {
            if (managerType.equals(DeliveryManagerType.HUB_DELIVERY_MANAGER)) {         // 허브 배송 담당자인 경우
                builder.and(qDeliveryRoute.manager.id.eq(managerId));
            }
            if (managerType.equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {     // 업체 배송 담당자인 경우
                builder.and(qDelivery.manager.id.eq(managerId));
            }
        }

        // 조회 쿼리
        List<Delivery> deliveries = queryFactory.selectDistinct(qDelivery)
                .from(qDelivery)
                .join(qDeliveryRoute).on(qDeliveryRoute.delivery.eq(qDelivery)).fetchJoin()
                .join(qDeliveryManager).on(qDelivery.manager.id.eq(qDeliveryManager.id)).fetchJoin()
                .where(builder)
                .orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), Delivery.class))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        // Count 쿼리
        Long total = queryFactory.select(qDelivery.id.count())
                .from(qDelivery)
                .join(qDeliveryRoute).on(qDeliveryRoute.delivery.eq(qDelivery)).fetchJoin()
                .join(qDeliveryManager).on(qDelivery.manager.id.eq(qDeliveryManager.id)).fetchJoin()
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                deliveries,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                total != null ? total : 0L);

    }

    /**
     * 배송 경로 검색
     * @param hubId 허브 id
     * @param companyId 업체 id
     * @param managerId 배송 담당자 id
     * @param managerType   배송 담당자 타입
     * @param pageable customPageable
     * @return 배송 경로
     */
    @Override
    public Page<DeliveryRoute> searchRoute(UUID hubId,
                                           UUID companyId,
                                           UUID managerId,
                                           DeliveryManagerType managerType,
                                           Pageable pageable) {

        BooleanBuilder builder = new BooleanBuilder();

        if (hubId != null) {        // 허브 관리자가 허브 id로 조회
            builder.and(qDeliveryRoute.departureHubId.eq(hubId));
        }

        if (companyId != null) {    // 업체 관리자가 업체 id로 조회
            builder.and(qDeliveryRoute.manager.companyId.eq(companyId));
        }

        if (managerId != null && managerType != null) {

            if (managerType.equals(DeliveryManagerType.HUB_DELIVERY_MANAGER)) {         // 허브 배송 담당자인 경우
                builder.and(qDeliveryRoute.manager.id.eq(managerId));
            }
            if (managerType.equals(DeliveryManagerType.COMPANY_DELIVERY_MANAGER)) {     // 업체 배송 담당자인 경우
                builder.and(qDeliveryRoute.delivery.manager.id.eq(managerId));
            }

        }

        // 조회 쿼리
        List<DeliveryRoute> routes = queryFactory.selectDistinct(qDeliveryRoute)
                .from(qDeliveryRoute)
                .leftJoin(qDelivery).on(qDeliveryRoute.delivery.eq(qDelivery)).fetchJoin()
                .join(qDeliveryManager).on(qDelivery.manager.id.eq(qDeliveryManager.id)).fetchJoin()
                .where(builder)
                .orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), DeliveryRoute.class))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        // Count 쿼리
        Long total = queryFactory.select(qDeliveryRoute.id.count())
                .from(qDeliveryRoute)
                .leftJoin(qDelivery).on(qDeliveryRoute.delivery.eq(qDelivery)).fetchJoin()
                .join(qDeliveryManager).on(qDelivery.manager.id.eq(qDeliveryManager.id)).fetchJoin()
                .where(builder)
                .fetchOne();

        return new PageImpl<>(
                routes,
                PageRequest.of(pageable.getPageNumber(), pageable.getPageSize()),
                total != null ? total : 0L);

    }

    /**
     * 배송 경로 조회
     * @param routeId 배송 경로 id
     */
    @Override
    public Optional<DeliveryRoute> findRouteById(UUID routeId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(qDeliveryRoute)
                        .where(qDeliveryRoute.id.eq(routeId))
                        .fetchOne()
        );
    }

    /**
     * 배송 담당자 조회
     * @param managerId 배송 담당자 id
     */
    @Override
    public Optional<DeliveryManager> findManagerById(UUID managerId) {
        return Optional.ofNullable(
                queryFactory.selectFrom(qDeliveryManager)
                        .where(qDeliveryManager.id.eq(managerId))
                        .fetchOne()
        );

    }
}
