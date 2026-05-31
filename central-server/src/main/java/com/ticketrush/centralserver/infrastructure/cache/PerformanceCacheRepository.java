package com.ticketrush.centralserver.infrastructure.cache;

import java.time.Duration;
import java.util.Optional;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Repository
public class PerformanceCacheRepository {

	private final RedisTemplate<String, String> redisTemplate;

	public Optional<String> getPerformanceList(String key){
		return Optional.ofNullable(redisTemplate.opsForValue().get(key));
	}

	public void setPerformanceList(String key, String value, Duration ttl){
		redisTemplate.opsForValue().set(key, value, ttl);
	}

	public boolean acquireLock(String key, Duration ttl) {
		Boolean result = redisTemplate.opsForValue().setIfAbsent(key, "locked", ttl);
		return Boolean.TRUE.equals(result);
	}

	public void releaseLock(String key) {
		redisTemplate.delete(key);
	}

}
