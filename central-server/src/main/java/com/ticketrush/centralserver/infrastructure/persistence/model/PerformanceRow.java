package com.ticketrush.centralserver.infrastructure.persistence.model;

public record PerformanceRow(
	Long id,
	String title,
	String venue,
	Integer totalSeats
) {
}
