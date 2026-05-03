package com.ticketrush.centralserver.application.schedule;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketrush.centralserver.infrastructure.persistence.mapper.ScheduleQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.ScheduleRow;
import com.ticketrush.centralserver.interfaces.api.performance.dto.ScheduleResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ScheduleQueryService {

	private final ScheduleQueryMapper scheduleQueryMapper;

	public List<ScheduleResponse> getSchedules(Long performanceId) {

		return scheduleQueryMapper.findByPerformanceId(performanceId).stream()
			.map(this::toResponse)
			.toList();

	}

	private ScheduleResponse toResponse(ScheduleRow row) {
		return new ScheduleResponse(
			row.id(),
			row.performanceId(),
			row.startTime(),
			row.endTime(),
			row.status()
		);
	}

}
