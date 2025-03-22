package com.oringmaryho.business.slackservice.exception;

import lombok.Getter;

@Getter
public class SlackException extends RuntimeException{

	private final ErrorCode errorCode;

	public SlackException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
