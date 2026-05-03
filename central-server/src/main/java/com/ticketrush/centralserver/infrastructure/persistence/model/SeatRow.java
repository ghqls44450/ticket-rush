package com.ticketrush.centralserver.infrastructure.persistence.model;

public record SeatRow(
	Long id,
	Long scheduleId,
	String seatNumber,
	String status,
	Integer price
) {
}
