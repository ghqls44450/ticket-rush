package com.ticketrush.centralserver.interfaces.api.seat.dto;

public record SeatReleaseResponse(
	Long seatId,
	String status
) {
}
