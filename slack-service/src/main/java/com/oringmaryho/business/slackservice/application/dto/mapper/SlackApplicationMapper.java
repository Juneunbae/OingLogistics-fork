package com.oringmaryho.business.slackservice.application.dto.mapper;

import java.util.UUID;

import org.mapstruct.Mapper;

import com.oringmaryho.business.slackservice.presentation.dto.request.SlackMessageUpdateResponseDto;

@Mapper(componentModel = "spring")
public interface SlackApplicationMapper {

	SlackMessageUpdateResponseDto toSlackMessageUpdateResponseDto(UUID id);
}
