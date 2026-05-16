package com.ticketrush.centralserver.application.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketrush.centralserver.application.payment.PaymentCreateCommand;
import com.ticketrush.centralserver.domain.seat.SeatStatus;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.PaymentQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.ReservationQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.reservation.dto.ReservationConfirmResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReservationConfirmService {

	private final SeatQueryMapper seatQueryMapper;
	private final ReservationQueryMapper reservationQueryMapper;
	private final PaymentQueryMapper paymentQueryMapper;

	@Transactional
	public ReservationConfirmResponse confirmReservation(ReservationConfirmCommand command) {

		SeatRow seat = seatQueryMapper.findByIdForUpdate(command.seatId())
			.orElseThrow(() -> new ApiException(ErrorCode.SEAT_NOT_FOUND));

		SeatStatus currentStatus = SeatStatus.from(seat.status());

		if (!currentStatus.canTransitionTo(SeatStatus.CONFIRMED)) {
			throw new ApiException(ErrorCode.SEAT_CANNOT_BE_CONFIRMED);
		}

		reservationQueryMapper.insertReservation(command);
		Long reservationId = reservationQueryMapper.findIdBySeatId(command.seatId())
			.orElseThrow(() -> new ApiException(ErrorCode.INTERNAL_SERVER_ERROR));

		paymentQueryMapper.insertPayment(new PaymentCreateCommand(reservationId, seat.price(), "PENDING", null));
		Long paymentId = paymentQueryMapper.findIdByReservationId(reservationId)
			.orElseThrow(() -> new ApiException(ErrorCode.INTERNAL_SERVER_ERROR));

		int confirmCount = seatQueryMapper.confirmSeat(seat.id());
		if (confirmCount != 1) {
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		return new ReservationConfirmResponse(reservationId, paymentId, command.seatId(), command.userId(), "CONFIRMED", "PENDING",
			seat.price());

	}
}
