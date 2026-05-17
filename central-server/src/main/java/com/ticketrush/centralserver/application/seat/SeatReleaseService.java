package com.ticketrush.centralserver.application.seat;

import org.springframework.stereotype.Service;

import com.ticketrush.centralserver.interfaces.api.seat.dto.SeatReleaseResponse;

@Service
public class SeatReleaseService {

	public SeatReleaseResponse releaseSeat(SeatReleaseCommand command) {

		return new SeatReleaseResponse(command.seatId(), "AVAILABLE");
	}

}
