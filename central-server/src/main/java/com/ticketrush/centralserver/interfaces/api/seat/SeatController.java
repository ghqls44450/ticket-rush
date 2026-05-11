package com.ticketrush.centralserver.interfaces.api.seat;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketrush.centralserver.application.seat.SeatHoldService;
import com.ticketrush.centralserver.interfaces.api.seat.dto.SeatHoldResponse;
import com.ticketrush.centralserver.support.response.ApiResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/seats")
@RequiredArgsConstructor
public class SeatController {

	private final SeatHoldService seatHoldService;

	@PostMapping("/{seatId}/hold")
	public ApiResponse<SeatHoldResponse> holdSeat(@PathVariable Long seatId) {
		return ApiResponse.success(seatHoldService.holdSeat(seatId));
	}

}
