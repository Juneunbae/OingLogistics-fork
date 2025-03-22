package com.oringmaryho.business.slackservice.domain;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SlackMessageSearchCriteria {

  private UUID id;
  private Long receiverId;
  private String message;
  private LocalDateTime sentAt;
}
