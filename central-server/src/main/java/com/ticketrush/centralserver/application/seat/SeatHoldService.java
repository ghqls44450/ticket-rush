package com.ticketrush.centralserver.application.seat;

import java.time.Duration;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketrush.centralserver.domain.seat.SeatStatus;
import com.ticketrush.centralserver.infrastructure.cache.SeatHoldCacheRepository;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.interfaces.api.seat.dto.SeatHoldResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SeatHoldService {

	private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(5);

	private final SeatQueryMapper seatQueryMapper;
	private final SeatHoldCacheRepository seatHoldCacheRepository;

	@Transactional
	public SeatHoldResponse holdSeat(Long seatId) {

		int updatedCount = seatQueryMapper.holdSeatIfAvailable(seatId);

		if (updatedCount == 1) {

			seatHoldCacheRepository.saveHold(seatId, SEAT_HOLD_TTL);

			return new SeatHoldResponse(seatId, SeatStatus.HELD.name());
		}

		seatQueryMapper.findById(seatId)
			.orElseThrow(() -> new ApiException(ErrorCode.SEAT_NOT_FOUND));

		throw new ApiException(ErrorCode.SEAT_CANNOT_BE_HELD);
	}
}
