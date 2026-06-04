package com.ticketrush.centralserver.application.reservation;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ticketrush.centralserver.application.payment.PaymentCreateCommand;
import com.ticketrush.centralserver.domain.seat.SeatStatus;
import com.ticketrush.centralserver.infrastructure.cache.SeatHoldCacheRepository;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.PaymentQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.ReservationQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.reservation.dto.ReservationConfirmResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class ReservationConfirmService {

	private static final String CONFIRMED = "CONFIRMED";
	private static final String PENDING = "PENDING";

	private final SeatQueryMapper seatQueryMapper;
	private final ReservationQueryMapper reservationQueryMapper;
	private final PaymentQueryMapper paymentQueryMapper;
	private final SeatHoldCacheRepository seatHoldCacheRepository;

	@Transactional
	public ReservationConfirmResponse confirmReservation(ReservationConfirmCommand command) {

		SeatRow seat = seatQueryMapper.findByIdForUpdate(command.seatId())
			.orElseThrow(() -> new ApiException(ErrorCode.SEAT_NOT_FOUND));

		SeatStatus currentStatus = SeatStatus.from(seat.status());

		if (!currentStatus.canTransitionTo(SeatStatus.CONFIRMED)) {
			throw new ApiException(ErrorCode.SEAT_CANNOT_BE_CONFIRMED);
		}
		Integer amount = seat.price();

		reservationQueryMapper.insertReservation(command);
		Long reservationId = reservationQueryMapper.findIdBySeatId(command.seatId())
			.orElseThrow(() -> new ApiException(ErrorCode.INTERNAL_SERVER_ERROR));

		PaymentCreateCommand paymentCommand = new PaymentCreateCommand(
			reservationId,
			amount,
			PENDING,
			null
		);
		paymentQueryMapper.insertPayment(paymentCommand);
		Long paymentId = paymentQueryMapper.findIdByReservationId(reservationId)
			.orElseThrow(() -> new ApiException(ErrorCode.INTERNAL_SERVER_ERROR));

		int confirmCount = seatQueryMapper.confirmSeat(seat.id());
		if (confirmCount != 1) {
			throw new ApiException(ErrorCode.INTERNAL_SERVER_ERROR);
		}

		try {
			seatHoldCacheRepository.deleteHold(seat.id());
		} catch (Exception e) {
			log.warn("Redis hold key 삭제 실패. seatId={}", seat.id(), e);
		}

		return new ReservationConfirmResponse(
			reservationId,
			paymentId,
			command.seatId(),
			command.userId(),
			CONFIRMED,
			PENDING,
			amount
		);

	}
}
