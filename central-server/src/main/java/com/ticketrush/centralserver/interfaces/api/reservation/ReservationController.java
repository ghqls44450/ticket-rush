package com.ticketrush.centralserver.interfaces.api.reservation;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ticketrush.centralserver.application.reservation.ReservationConfirmService;
import com.ticketrush.centralserver.interfaces.api.reservation.dto.ReservationConfirmRequest;
import com.ticketrush.centralserver.interfaces.api.reservation.dto.ReservationConfirmResponse;
import com.ticketrush.centralserver.support.response.ApiResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/reservations")
@RequiredArgsConstructor
public class ReservationController {

	private final ReservationConfirmService reservationConfirmService;

	@PostMapping("/confirm")
	public ApiResponse<ReservationConfirmResponse> confirmReservation(@Valid @RequestBody ReservationConfirmRequest request) {
		return ApiResponse.success(reservationConfirmService.confirmReservation(request.toCommand()));
	}
}
