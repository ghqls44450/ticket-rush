package com.ticketrush.centralserver.support.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_INTERNAL_ERROR", "서버 내부 오류가 발생했습니다."),
	VALIDATION_ERROR(HttpStatus.BAD_REQUEST, "COMMON_INVALID_INPUT", "잘못된 요청입니다."),
	PERFORMANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "PERFORMANCE_NOT_FOUND", "공연을 찾을 수 없습니다."),

	;

	private final HttpStatus status;
	private final String code;
	private final String defaultMessage;

	ErrorCode(HttpStatus status, String code, String defaultMessage) {
		this.status = status;
		this.code = code;
		this.defaultMessage = defaultMessage;
	}

	public HttpStatus status() { return status; }
	public String code() { return code; }
	public String defaultMessage() { return defaultMessage; }
}
