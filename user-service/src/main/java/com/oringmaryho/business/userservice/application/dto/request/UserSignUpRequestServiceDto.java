package com.oringmaryho.business.userservice.application.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignUpRequestServiceDto {

  private String username;
  private String password;
  private String slackId;
}
