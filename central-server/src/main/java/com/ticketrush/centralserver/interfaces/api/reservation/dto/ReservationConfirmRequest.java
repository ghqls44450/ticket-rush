package com.ticketrush.centralserver.interfaces.api.reservation.dto;

import com.ticketrush.centralserver.application.reservation.ReservationConfirmCommand;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReservationConfirmRequest(
	@NotNull
	@Positive
	Long userId,

	@NotNull
	@Positive
	Long seatId
) {
	public ReservationConfirmCommand toCommand() {
		return new ReservationConfirmCommand(userId, seatId);
	}
}
