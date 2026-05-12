package com.ticketrush.centralserver.application.seat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketrush.centralserver.domain.seat.SeatStatus;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.seat.dto.SeatHoldResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SeatHoldService {

	private final SeatQueryMapper seatQueryMapper;

	@Transactional
	public SeatHoldResponse holdSeat(Long seatId) {
		SeatRow seat = seatQueryMapper.findByIdForUpdate(seatId)
			.orElseThrow(() -> new ApiException(ErrorCode.SEAT_NOT_FOUND));

		SeatStatus currentStatus = SeatStatus.valueOf(seat.status());

		if (!currentStatus.canTransitionTo(SeatStatus.HELD)) {
			throw new ApiException(ErrorCode.SEAT_CANNOT_BE_HELD);
		}

		seatQueryMapper.holdSeat(seatId);

		return new SeatHoldResponse(seat.id(), SeatStatus.HELD.name());
	}
}
