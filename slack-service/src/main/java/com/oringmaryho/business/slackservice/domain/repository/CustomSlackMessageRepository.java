package com.oringmaryho.business.slackservice.domain.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.oringmaryho.business.slackservice.domain.SlackMessage;
import com.oringmaryho.business.slackservice.domain.SlackMessageSearchCriteria;

public interface CustomSlackMessageRepository {

	Page<SlackMessage> findDynamicQuery(SlackMessageSearchCriteria criteria, Pageable pageable);

	Optional<SlackMessage> findActiveSlackMessageById(UUID id);
}
