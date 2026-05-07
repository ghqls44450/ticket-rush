package com.ticketrush.centralserver.application.seat;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketrush.centralserver.domain.seat.SeatStatus;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.schedule.dto.SeatResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SeatQueryService {

	private final SeatQueryMapper seatQueryMapper;

	public List<SeatResponse> getSeats(Long scheduleId, String status) {

		if (!SeatStatus.isValid(status)){
			throw new ApiException(ErrorCode.INVALID_SEAT_STATUS);
		}

		return seatQueryMapper.findByScheduleIdAndStatus(scheduleId, status).stream()
			.map(this::toResponse)
			.toList();
	}

	private SeatResponse toResponse(SeatRow row) {
		return new SeatResponse(
			row.id(),
			row.scheduleId(),
			row.seatNumber(),
			row.status(),
			row.price()
		);
	}

}
