package com.ticketrush.centralserver.domain.seat;

import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

public enum SeatStatus {

	AVAILABLE,
	HELD,
	CONFIRMED,
	CANCELLED
	;

	public boolean canTransitionTo(SeatStatus nextStatus) {
		if (nextStatus == null) {
			return false;
		}

		return switch (this) {
			case AVAILABLE -> nextStatus == HELD;
			case HELD -> nextStatus == CONFIRMED || nextStatus == AVAILABLE;
			case CONFIRMED -> nextStatus == CANCELLED;
			case CANCELLED -> false;
		};
	}

	public static boolean isValid(String value) {
		if (value == null) {
			return false;
		}

		if (value.isBlank()) {
			return false;
		}

		for (SeatStatus status : values()) {
			if (status.name().equals(value)) {
				return true;
			}
		}

		return false;
	}

	public static SeatStatus from(String value) {
		if (!isValid(value)) {
			throw new ApiException(ErrorCode.INVALID_SEAT_STATUS);
		}

		return valueOf(value);
	}


}
