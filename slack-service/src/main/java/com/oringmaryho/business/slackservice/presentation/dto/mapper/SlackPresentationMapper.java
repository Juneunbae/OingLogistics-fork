package com.oringmaryho.business.slackservice.presentation.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oringmaryho.business.slackservice.application.dto.request.SlackAdminMessageCreateRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageDeleteRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageFindRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageSearchRequestServiceDto;
import com.oringmaryho.business.slackservice.application.dto.request.SlackMessageUpdateRequestServiceDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackAdminMessageCreateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateRequestDto;
import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageSearchRequestDto;

@Mapper(componentModel = "spring")
public interface SlackPresentationMapper {

	SlackMessageSearchRequestServiceDto toSlackMessageSearchRequestServiceDto(
		SlackMessageSearchRequestDto requestDto);

	SlackMessageFindRequestServiceDto toSlackMessageFindRequestServiceDto(UUID id);

	SlackMessageUpdateRequestServiceDto toSlackMessageUpdateRequestServiceDto(UUID id,
		SlackMessageUpdateRequestDto requestDto);

	SlackMessageDeleteRequestServiceDto toSlackMessageDeleteRequestServiceDto(Long userId, UUID id);

	SlackAdminMessageCreateRequestServiceDto toSlackAdminMessageCreateRequestServiceDto(
		SlackAdminMessageCreateRequestDto requestDto);
}
