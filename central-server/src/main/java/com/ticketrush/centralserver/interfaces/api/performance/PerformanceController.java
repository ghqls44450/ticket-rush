package com.ticketrush.centralserver.interfaces.api.performance;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketrush.centralserver.application.performance.PerformanceQueryService;
import com.ticketrush.centralserver.application.schedule.ScheduleQueryService;
import com.ticketrush.centralserver.interfaces.api.performance.dto.PerformanceResponse;
import com.ticketrush.centralserver.interfaces.api.performance.dto.ScheduleResponse;
import com.ticketrush.centralserver.support.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/performances")
@RequiredArgsConstructor
public class PerformanceController {

	private final PerformanceQueryService performanceQueryService;
	private final ScheduleQueryService scheduleQueryService;

	@GetMapping
	public ApiResponse<List<PerformanceResponse>> getPerformances() {
		return ApiResponse.success(performanceQueryService.getPerformances());
	}

	@GetMapping("/{performanceId}")
	public ApiResponse<PerformanceResponse> getPerformance(@PathVariable Long performanceId) {
		return ApiResponse.success(performanceQueryService.getPerformance(performanceId));
	}

	@GetMapping("/{performanceId}/schedules")
	public ApiResponse<List<ScheduleResponse>> getSchedules(@PathVariable Long performanceId) {
		return ApiResponse.success(scheduleQueryService.getSchedules(performanceId));
	}

}
