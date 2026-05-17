package com.ticketrush.centralserver.application.seat;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketrush.centralserver.domain.seat.SeatStatus;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.seat.dto.SeatReleaseResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SeatReleaseService {

	private static final String AVAILABLE = "AVAILABLE";

	private final SeatQueryMapper seatQueryMapper;

	@Transactional
	public SeatReleaseResponse releaseSeat(SeatReleaseCommand command) {

		Long seatId = command.seatId();

		SeatRow seat = seatQueryMapper.findByIdForUpdate(seatId)
			.orElseThrow(() -> new ApiException(ErrorCode.SEAT_NOT_FOUND));

		SeatStatus currentStatus = SeatStatus.from(seat.status());

		if (!currentStatus.canTransitionTo(SeatStatus.AVAILABLE)) {
			throw new ApiException(ErrorCode.SEAT_CANNOT_BE_RELEASED);
		}

		int releaseCount = seatQueryMapper.releaseSeat(seatId);

		if (releaseCount != 1) {
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return new SeatReleaseResponse(seatId, AVAILABLE);
	}

}
