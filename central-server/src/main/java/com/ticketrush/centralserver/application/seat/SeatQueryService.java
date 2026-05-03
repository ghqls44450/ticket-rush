package com.ticketrush.centralserver.application.seat;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.schedule.dto.SeatResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class SeatQueryService {

	private final SeatQueryMapper seatQueryMapper;

	public List<SeatResponse> getSeats(Long scheduleId, String status) {
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
