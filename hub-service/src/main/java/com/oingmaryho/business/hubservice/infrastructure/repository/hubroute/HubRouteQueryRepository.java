package com.oingmaryho.business.hubservice.infrastructure.repository.hubroute;

import static com.oingmaryho.business.hubservice.domain.QHubRoute.*;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oingmaryho.business.hubservice.domain.HubRoute;
import com.oingmaryho.business.hubservice.domain.criteria.HubRouteSearchCriteria;
import com.oingmaryho.business.hubservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class HubRouteQueryRepository {

	private final JPAQueryFactory queryFactory;

	public Page<HubRoute> findDynamicQuery(HubRouteSearchCriteria criteria, Pageable pageable) {
		BooleanBuilder conditions = new BooleanBuilder();
		conditions
			.and(eqId(criteria.getId()))
			.and(eqDepartureHubId(criteria.getDepartureHubId()))
			.and(eqArriveHubId(criteria.getArriveHubId()))
			.and(eqIsDeleted(criteria.getIsDeleted()));

		JPAQuery<HubRoute> query = queryFactory
			.selectFrom(hubRoute)
			.where(conditions)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), HubRoute.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		Long total = queryFactory
			.select(hubRoute.id.count())
			.from(hubRoute)
			.where(conditions)
			.fetchOne();
		assert total != null;

		return new PageImpl<>(query.fetch(), pageable, total);
	}

	public List<HubRoute> findAllAssociatedWithHub(UUID hubId) {
		BooleanBuilder conditions = new BooleanBuilder();
		conditions
			.or(eqDepartureHubId(hubId))
			.or(eqArriveHubId(hubId));

		return queryFactory
			.selectFrom(hubRoute)
			.where(conditions)
			.fetch();
	}

	private BooleanExpression eqId(UUID id) {
		if(id == null) {
			return null;
		}
		return hubRoute.id.eq(id);
	}

	private BooleanExpression eqDepartureHubId(UUID departureHubId) {
		if(departureHubId == null) {
			return null;
		}
		return hubRoute.departureHubId.eq(departureHubId);
	}

	private BooleanExpression eqArriveHubId(UUID arriveHubId) {
		if(arriveHubId == null) {
			return null;
		}
		return hubRoute.arriveHubId.eq(arriveHubId);
	}

	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if(isDeleted == null) {
			return null;
		}
		return hubRoute.isDeleted.eq(isDeleted);
	}
}
