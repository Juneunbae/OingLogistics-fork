package com.oringmaryho.business.userservice.infrastructure;

import static com.oringmaryho.business.userservice.domain.QUser.*;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import com.oringmaryho.business.userservice.domain.User;
import com.oringmaryho.business.userservice.domain.UserConfirmStatus;
import com.oringmaryho.business.userservice.domain.UserRoleType;
import com.oringmaryho.business.userservice.domain.UserSearchCriteria;
import com.oringmaryho.business.userservice.domain.repository.CustomUserRepository;
import com.oringmaryho.business.userservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class UserQueryRepository implements CustomUserRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<User> findDynamicQuery(UserSearchCriteria criteria, Pageable pageable) {
		BooleanBuilder conditions = new BooleanBuilder();
		conditions
			.and(eqId(criteria.getId()))
			.and(eqUsername(criteria.getUsername()))
			.and(eqSlackId(criteria.getSlackId()))
			.and(eqRole(criteria.getRole()))
			.and(eqStatus(criteria.getStatus()))
			.and(eqIsDeleted(criteria.getIsDeleted()));

		JPAQuery<User> query = queryFactory
			.selectFrom(user)
			.where(conditions)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), User.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		Long total = queryFactory
			.select(user.id.count())
			.from(user)
			.where(conditions)
			.fetchOne();
		assert total != null;

		return new PageImpl<>(query.fetch(), pageable, total);
	}

	@Override
	public Optional<User> findActiveUserById(Long id) {
		return Optional.ofNullable(queryFactory
			.selectFrom(user)
			.where(
				eqId(id),
				eqIsDeleted(Boolean.FALSE)
			)
			.fetchOne()
		);
	}

	private BooleanExpression eqId(Long id) {
		if (id == null) {
			return null;
		}
		return user.id.eq(id);
	}

	private BooleanExpression eqUsername(String username) {
		if (!StringUtils.hasText(username)) {
			return null;
		}
		return user.username.contains(username);
	}

	private BooleanExpression eqSlackId(String slackId) {
		if (!StringUtils.hasText(slackId)) {
			return null;
		}
		return user.slackId.contains(slackId);
	}

	private BooleanExpression eqRole(UserRoleType role) {
		if (role == null) {
			return null;
		}
		return user.role.eq(role);
	}

	private BooleanExpression eqStatus(UserConfirmStatus status) {
		if (status == null) {
			return null;
		}
		return user.status.eq(status);
	}

	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		return user.isDeleted.eq(isDeleted);
	}
}
