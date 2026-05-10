package com.ticketrush.centralserver.interfaces.api.seat.dto;

public record SeatHoldResponse(
	Long seatId,
	String status
) {
}
