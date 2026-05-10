package com.ticketrush.centralserver.domain.seat;

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
