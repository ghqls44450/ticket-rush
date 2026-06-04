package com.ticketrush.centralserver.application.seat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SeatHoldExpireSchedulerTest {

	@Mock
	private SeatReleaseService seatReleaseService;

	private SeatHoldExpireScheduler seatHoldExpireScheduler;

	@BeforeEach
	void setUp() {
		seatHoldExpireScheduler = new SeatHoldExpireScheduler(seatReleaseService);
	}

	@Test
	@DisplayName("만료된 홀드 복구 스케줄이 5분 전 기준으로 복구를 요청한다")
	void 만료된_홀드_복구_스케줄이_5분_전_기준으로_복구를_요청한다() {

		LocalDateTime beforeCall = LocalDateTime.now();

		seatHoldExpireScheduler.releaseExpiredSeatHolds();

		LocalDateTime afterCall = LocalDateTime.now();

		ArgumentCaptor<LocalDateTime> thresholdCaptor = ArgumentCaptor.forClass(LocalDateTime.class);
		verify(seatReleaseService).releaseExpiredSeats(thresholdCaptor.capture());

		LocalDateTime capturedThreshold = thresholdCaptor.getValue();

		assertFalse(capturedThreshold.isBefore(beforeCall.minusMinutes(5)));
		assertFalse(capturedThreshold.isAfter(afterCall.minusMinutes(5)));

	}


}
