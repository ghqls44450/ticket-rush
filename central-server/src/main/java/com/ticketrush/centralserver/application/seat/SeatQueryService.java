package com.ticketrush.centralserver.application.seat;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketrush.centralserver.domain.seat.SeatStatus;
import com.ticketrush.centralserver.infrastructure.config.datasource.RoutingDataSourceContext;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.schedule.dto.SeatResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class SeatQueryService {

	private final SeatQueryMapper seatQueryMapper;

	public List<SeatResponse> getSeats(Long scheduleId, String status) {

		if (status != null && !SeatStatus.isValid(status)) {
			throw new ApiException(ErrorCode.INVALID_SEAT_STATUS);
		}

		return seatQueryMapper.findByScheduleIdAndStatus(scheduleId, status).stream()
			.map(this::toResponse)
			.toList();
	}

	public List<SeatResponse> getLatestSeats(Long scheduleId, String status) {
		RoutingDataSourceContext.forceMaster();

		try {
			return getSeats(scheduleId, status);
		} finally {
			RoutingDataSourceContext.clear();
		}
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
