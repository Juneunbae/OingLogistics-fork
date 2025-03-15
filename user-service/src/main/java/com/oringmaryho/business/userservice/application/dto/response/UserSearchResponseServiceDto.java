package com.oringmaryho.business.userservice.application.dto.response;

import com.oringmaryho.business.userservice.domain.UserRoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserSearchResponseServiceDto {

  private Long userId;
  private String userName;
  private String slackId;
  private UserRoleType role;
}
