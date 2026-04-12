package com.ticketrush.centralserver.support.exception;

public class ApiException extends RuntimeException {
	private final ErrorCode errorCode;

	public ApiException(ErrorCode errorCode) {
		super(errorCode.defaultMessage());
		this.errorCode = errorCode;
	}

	public ApiException(ErrorCode errorCode, String defaultMessage) {
		super(defaultMessage);
		this.errorCode = errorCode;
	}

	public ErrorCode getErrorCode() {
		return errorCode;
	}
}
