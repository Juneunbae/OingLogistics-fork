package com.oringmaryho.business.userservice.presentation.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignInResponseDto {

  private String accessToken;
  private String refreshToken;
}
