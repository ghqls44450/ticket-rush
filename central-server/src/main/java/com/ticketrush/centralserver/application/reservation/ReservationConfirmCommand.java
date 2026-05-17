package com.ticketrush.centralserver.application.reservation;

public record ReservationConfirmCommand(
	Long userId,
	Long seatId
) {
}
