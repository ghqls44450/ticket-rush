package com.ticketrush.centralserver.application.reservation;

import org.springframework.stereotype.Service;

import com.ticketrush.centralserver.interfaces.api.reservation.dto.ReservationConfirmResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReservationConfirmService {

	public ReservationConfirmResponse confirmReservation(ReservationConfirmCommand command) {

		return new ReservationConfirmResponse(null, null, command.seatId(), command.userId(), null, null, null);

	}

}
