package com.ticketrush.centralserver.support.exception;

import com.ticketrush.centralserver.support.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ApiException.class)
	public ResponseEntity<ApiResponse<Void>> handleApiException(ApiException e) {
		ErrorCode errorCode = e.getErrorCode();
		return ResponseEntity.status(errorCode.status())
			.body(ApiResponse.failure(errorCode, e.getMessage()));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {
		String message = e.getBindingResult().getFieldErrors().stream()
			.findFirst()
			.map(FieldError::getDefaultMessage)
			.orElse(ErrorCode.VALIDATION_ERROR.defaultMessage());

		return ResponseEntity.status(ErrorCode.VALIDATION_ERROR.status())
			.body(ApiResponse.failure(ErrorCode.VALIDATION_ERROR, message));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
		return ResponseEntity.status(ErrorCode.INTERNAL_SERVER_ERROR.status())
			.body(ApiResponse.failure(ErrorCode.INTERNAL_SERVER_ERROR));
	}
}
