package com.oringmaryho.business.slackservice.application.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oringmaryho.business.slackservice.domain.SlackMessage;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateResponseDto;
import com.oringmaryho.business.slackservice.presentation.dto.response.SlackMessageResponseDto;

@Mapper(componentModel = "spring")
public interface SlackApplicationMapper {

	SlackMessageUpdateResponseDto toSlackMessageUpdateResponseDto(UUID id);

	@Mapping(target = "id", source = "id")
	@Mapping(target = "receiverId", source = "receiverId")
	@Mapping(target = "message", source = "message")
	@Mapping(target = "sentAt", source = "sentAt")
	SlackMessageResponseDto toSlackMessageResponseDto(SlackMessage slackMessage);
}
