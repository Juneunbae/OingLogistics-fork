package com.oringmaryho.business.slackservice.infrastructure;

import static com.oringmaryho.business.slackservice.domain.QSlackMessage.*;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.flywaydb.core.internal.util.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.oringmaryho.business.slackservice.domain.SlackMessage;
import com.oringmaryho.business.slackservice.domain.SlackMessageSearchCriteria;
import com.oringmaryho.business.slackservice.domain.repository.CustomSlackMessageRepository;
import com.oringmaryho.business.slackservice.utils.QueryDslUtils;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class SlackMessageQueryRepository implements CustomSlackMessageRepository {

	private final JPAQueryFactory queryFactory;

	@Override
	public Page<SlackMessage> findDynamicQuery(SlackMessageSearchCriteria criteria, Pageable pageable) {
		BooleanBuilder conditions = new BooleanBuilder();
		conditions
			.and(eqId(criteria.getId()))
			.and(eqReceiverId(criteria.getReceiverId()))
			.and(eqMessage(criteria.getMessage()))
			.and(eqSentAt(criteria.getSentAt()))
			.and(eqIsDeleted(criteria.getIsDeleted()));

		JPAQuery<SlackMessage> query = queryFactory
			.selectFrom(slackMessage)
			.where(conditions)
			.orderBy(QueryDslUtils.getOrderSpecifiers(pageable.getSort(), SlackMessage.class))
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize());

		Long total = queryFactory
			.select(slackMessage.id.count())
			.from(slackMessage)
			.where(conditions)
			.fetchOne();
		assert total != null;

		return new PageImpl<>(query.fetch(), pageable, total);
	}

	@Override
	public Optional<SlackMessage> findActiveSlackMessageById(UUID id) {
		return Optional.ofNullable(queryFactory
			.selectFrom(slackMessage)
			.where(
				eqId(id),
				eqIsDeleted(Boolean.FALSE)
			)
			.fetchOne()
		);
	}

	private BooleanExpression eqId(UUID id) {
		if (id == null) {
			return null;
		}
		return slackMessage.id.eq(id);
	}

	private BooleanExpression eqReceiverId(Long receiverId) {
		if (receiverId == null) {
			return null;
		}
		return slackMessage.receiverId.eq(receiverId);
	}

	private BooleanExpression eqMessage(String message) {
		if (!StringUtils.hasText(message)) {
			return null;
		}
		return slackMessage.message.containsIgnoreCase(message.trim());
	}

	private BooleanExpression eqSentAt(LocalDateTime sentAt) {
		if (sentAt == null) {
			return null;
		}
		return slackMessage.sentAt.eq(sentAt);
	}
	private BooleanExpression eqIsDeleted(Boolean isDeleted) {
		if (isDeleted == null) {
			return null;
		}
		return slackMessage.isDeleted.eq(isDeleted);
	}
}
