package com.oringmaryho.business.userservice.presentation.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSignInRequestDto {

  private String username;
  private String password;
}
