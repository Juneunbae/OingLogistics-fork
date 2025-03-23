package com.oringmaryho.business.slackservice.presentation.dto.mapper;

import java.time.LocalDateTime;
import java.util.UUID;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDeleteRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageFindRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageSearchRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageUpdateRequestServiceDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageSearchRequestDto;

@Mapper(componentModel = "spring")
public interface SlackPresentationMapper {

	SlackMessageSearchRequestServiceDto toSlackMessageSearchRequestServiceDto(
		SlackMessageSearchRequestDto requestDto);

	SlackMessageFindRequestServiceDto toSlackMessageFindRequestServiceDto(UUID id);

	SlackMessageUpdateRequestServiceDto toSlackMessageUpdateRequestServiceDto(UUID id,
		SlackMessageRequestDto requestDto);

	SlackMessageDeleteRequestServiceDto toSlackMessageDeleteRequestServiceDto(Long userId, UUID id);

	SlackAdminMessageCreateRequestServiceDto toSlackAdminMessageCreateRequestServiceDto(
		SlackAdminMessageCreateRequestDto requestDto);
}
