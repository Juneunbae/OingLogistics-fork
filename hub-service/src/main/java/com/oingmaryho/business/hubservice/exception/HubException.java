package com.oingmaryho.business.hubservice.exception;

import lombok.Getter;

@Getter
public class HubException extends RuntimeException{

	private final ErrorCode errorCode;

	public HubException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}
}
