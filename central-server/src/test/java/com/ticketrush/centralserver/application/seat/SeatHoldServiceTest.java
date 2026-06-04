package com.ticketrush.centralserver.application.seat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.ticketrush.centralserver.infrastructure.cache.SeatHoldCacheRepository;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class SeatHoldServiceTest {

	private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(5);

	@Mock
	private SeatQueryMapper seatQueryMapper;

	@Mock
	private SeatHoldCacheRepository seatHoldCacheRepository;

	private SeatHoldService seatHoldService;

	@BeforeEach
	void setUp() {
		seatHoldService = new SeatHoldService(
			seatQueryMapper,
			seatHoldCacheRepository
		);
	}

	@Test
	@DisplayName("좌석 점유 성공 시 Redis 홀드 TTL을 저장한다")
	void 좌석_점유_성공_시_Redis_홀드_TTL_저장() {

		when(seatQueryMapper.holdSeatIfAvailable(1L))
			.thenReturn(1);

		seatHoldService.holdSeat(1L);

		verify(seatHoldCacheRepository, times(1))
			.saveHold(1L, SEAT_HOLD_TTL);
	}

	@Test
	@DisplayName("좌석 점유 실패 시 Redis 홀드 TTL을 저장하지 않는다")
	void 좌석_점유_실패_시_Redis_홀드_TTL_미저장() {

		when(seatQueryMapper.holdSeatIfAvailable(1L))
			.thenReturn(0);
		when(seatQueryMapper.findById(1L))
			.thenReturn(Optional.of(new SeatRow(1L, 1L, "A-1", "HELD", 10000)));

		ApiException exception = assertThrows(ApiException.class,
			() -> seatHoldService.holdSeat(1L));

		assertEquals(ErrorCode.SEAT_CANNOT_BE_HELD, exception.getErrorCode());

		verify(seatHoldCacheRepository, never())
			.saveHold(anyLong(), any(Duration.class));
	}
}