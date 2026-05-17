package com.ticketrush.centralserver.interfaces.api.reservation.dto;

public record ReservationConfirmResponse(
	Long reservationId,
	Long paymentId,
	Long seatId,
	Long userId,
	String reservationStatus,
	String paymentStatus,
	Integer amount
) {
}
