package com.ticketrush.centralserver.domain.seat;

public enum SeatStatus {

	AVAILABLE,
	HELD,
	CONFIRMED,
	CANCELLED
	;

	public static boolean isValid(String value) {
		if (value == null) {
			return true;
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


}
