package com.oingmaryho.business.common.exception;

import org.springframework.http.HttpStatus;

public interface BaseErrorCode {

	String getErrorCode();
	String getMessage();
	HttpStatus getStatus();
}
