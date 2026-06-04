package com.ticketrush.centralserver.application.reservation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ticketrush.centralserver.infrastructure.cache.SeatHoldCacheRepository;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.PaymentQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.ReservationQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.interfaces.api.reservation.dto.ReservationConfirmResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class ReservationConfirmServiceTest {

	@Mock
	private SeatQueryMapper seatQueryMapper;

	@Mock
	private ReservationQueryMapper reservationQueryMapper;

	@Mock
	private PaymentQueryMapper paymentQueryMapper;

	@Mock
	private SeatHoldCacheRepository seatHoldCacheRepository;

	private ReservationConfirmService reservationConfirmService;

	@BeforeEach
	void setUp() {
		reservationConfirmService = new ReservationConfirmService(
			seatQueryMapper,
			reservationQueryMapper,
			paymentQueryMapper,
			seatHoldCacheRepository
		);
	}

	@Test
	@DisplayName("예매 확정 성공 시 Redis hold key를 삭제한다")
	void 예매_확정_성공_시_Redis_hold_key를_삭제한다() {

		SeatRow heldSeat = new SeatRow(
			1L,
			1L,
			"A1",
			"HELD",
			50000
		);

		ReservationConfirmCommand command = new ReservationConfirmCommand(100L, 1L);

		when(seatQueryMapper.findByIdForUpdate(1L))
			.thenReturn(Optional.of(heldSeat));

		when(reservationQueryMapper.findIdBySeatId(1L))
			.thenReturn(Optional.of(10L));

		when(paymentQueryMapper.findIdByReservationId(10L))
			.thenReturn(Optional.of(20L));

		when(seatQueryMapper.confirmSeat(1L))
			.thenReturn(1);

		ReservationConfirmResponse response = reservationConfirmService.confirmReservation(command);

		assertEquals(10L, response.reservationId());
		assertEquals(20L, response.paymentId());
		assertEquals(1L, response.seatId());
		assertEquals(100L, response.userId());
		assertEquals("CONFIRMED", response.reservationStatus());
		assertEquals("PENDING", response.paymentStatus());
		assertEquals(50000, response.amount());
		verify(seatQueryMapper).confirmSeat(1L);
		verify(seatHoldCacheRepository).deleteHold(1L);
	}

	@Test
	@DisplayName("예매 확정 실패 시 Redis hold key를 삭제하지 않는다")
	void 예매_확정_실패_시_Redis_hold_key를_삭제하지_않는다() {
		SeatRow availableSeat = new SeatRow(
			1L,
			1L,
			"A1",
			"AVAILABLE",
			50000
		);

		ReservationConfirmCommand command = new ReservationConfirmCommand(100L, 1L);

		when(seatQueryMapper.findByIdForUpdate(1L))
			.thenReturn(Optional.of(availableSeat));

		ApiException exception = assertThrows(
			ApiException.class,
			() -> reservationConfirmService.confirmReservation(command)
		);

		assertEquals(ErrorCode.SEAT_CANNOT_BE_CONFIRMED, exception.getErrorCode());
		verify(seatHoldCacheRepository, never()).deleteHold(anyLong());
		verify(reservationQueryMapper, never()).insertReservation(any());
		verify(paymentQueryMapper, never()).insertPayment(any());
		verify(seatQueryMapper, never()).confirmSeat(anyLong());
	}
}
