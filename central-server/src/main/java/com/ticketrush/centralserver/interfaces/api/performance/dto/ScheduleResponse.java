package com.ticketrush.centralserver.interfaces.api.performance.dto;

import java.time.LocalDateTime;

public record ScheduleResponse(
	Long id,
	Long performanceId,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String status
) {
}
