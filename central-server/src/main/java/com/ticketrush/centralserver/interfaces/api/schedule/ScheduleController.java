package com.ticketrush.centralserver.interfaces.api.schedule;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ticketrush.centralserver.application.seat.SeatQueryService;
import com.ticketrush.centralserver.interfaces.api.schedule.dto.SeatResponse;
import com.ticketrush.centralserver.support.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/schedules")
@RequiredArgsConstructor
public class ScheduleController {

	private final SeatQueryService seatQueryService;

	@GetMapping("/{scheduleId}/seats")
	public ApiResponse<List<SeatResponse>> getSeats(@PathVariable Long scheduleId, @RequestParam(required = false) String status) {
		return ApiResponse.success(seatQueryService.getSeats(scheduleId, status));
	}


}
