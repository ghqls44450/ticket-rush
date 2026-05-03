package com.ticketrush.centralserver.interfaces.api.schedule.dto;

public record SeatResponse(
	Long id,
	Long scheduleId,
	String seatNumber,
	String status,
	Integer price
) {
}
