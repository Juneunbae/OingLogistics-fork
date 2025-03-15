package com.oringmaryho.business.userservice.application.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignInResponseServiceDto {

  private String accessToken;
  private String refreshToken;
}
