package com.ticketrush.centralserver.support.response;

import com.ticketrush.centralserver.support.exception.ErrorCode;

public record ApiResponse<T>(boolean success, T data, ErrorBody error) {
	public static <T> ApiResponse<T> success(T data) {
		return new ApiResponse<>(true, data, null);
	}

	public static ApiResponse<Void> failure(ErrorCode errorCode) {
		return new ApiResponse<>(false, null,
			new ErrorBody(errorCode.code(), errorCode.defaultMessage()));
	}

	public static ApiResponse<Void> failure(ErrorCode errorCode, String message) {
		return new ApiResponse<>(false, null,
			new ErrorBody(errorCode.code(), message));
	}


	public record ErrorBody(String code, String message) {
	}
}
