package com.ticketrush.centralserver.infrastructure.persistence.model;

import java.time.LocalDateTime;

public record ScheduleRow(
	Long id,
	Long performanceId,
	LocalDateTime startTime,
	LocalDateTime endTime,
	String status
) {
}

