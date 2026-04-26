package com.ticketrush.centralserver.interfaces.api.performance.dto;

public record PerformanceResponse(
	Long id,
	String title,
	String venue,
	Integer totalSeats
) {
}
