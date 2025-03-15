package com.oringmaryho.business.userservice.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSlackConfirmRequestDto {

  private String slackId;
  private String confirmCode;
}
