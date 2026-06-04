package com.ticketrush.centralserver.application.seat;

import java.time.Duration;
import java.time.LocalDateTime;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class SeatHoldExpireScheduler {

	private static final Duration SEAT_HOLD_TTL = Duration.ofMinutes(5);

	private final SeatReleaseService seatReleaseService;

	/*
	Redis TTL 자체를 직접 구독하지 않고,
    DB held_at 기준으로 만료된 HELD 좌석을 주기적으로 복구한다.
	 */
	@Scheduled(fixedDelay = 60000)
	public void releaseExpiredSeatHolds() {
		LocalDateTime threshold = LocalDateTime.now().minus(SEAT_HOLD_TTL);
		seatReleaseService.releaseExpiredSeats(threshold);
	}

}
