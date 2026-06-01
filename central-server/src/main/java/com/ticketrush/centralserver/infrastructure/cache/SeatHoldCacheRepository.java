package com.ticketrush.centralserver.infrastructure.cache;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class SeatHoldCacheRepository {

	private static final String SEAT_HOLD_KEY_PREFIX = "seat:hold:";

	private final RedisTemplate<String, String> redisTemplate;

	public void saveHold(Long seatId, Duration ttl) {
		redisTemplate.opsForValue().set(key(seatId), "HELD", ttl);
	}

	private String key(Long seatId) {
		return SEAT_HOLD_KEY_PREFIX + seatId;
	}

}
