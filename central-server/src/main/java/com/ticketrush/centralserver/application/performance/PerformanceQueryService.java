package com.ticketrush.centralserver.application.performance;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketrush.centralserver.infrastructure.cache.PerformanceCacheRepository;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.PerformanceQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.PerformanceRow;
import com.ticketrush.centralserver.interfaces.api.performance.dto.PerformanceResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class PerformanceQueryService {

	private static final String PERFORMANCE_LIST_KEY = "performance:list:v1";
	private static final String PERFORMANCE_LIST_LOCK_KEY = "performance:list:v1:lock";
	private static final Duration PERFORMANCE_LIST_TTL = Duration.ofSeconds(60);
	private static final Duration PERFORMANCE_LIST_LOCK_TTL = Duration.ofSeconds(60);

	private final ObjectMapper objectMapper;

	private final PerformanceCacheRepository performanceCacheRepository;
	private final PerformanceQueryMapper performanceQueryMapper;

	public List<PerformanceResponse> getPerformances() {

		Optional<String> cached = performanceCacheRepository.getPerformanceList(PERFORMANCE_LIST_KEY);

		if (cached.isPresent()) {
			log.info("공연 목록 캐시 hit - key={}", PERFORMANCE_LIST_KEY);
			return deserializePerformanceList(cached.get());
		}

		boolean locked = performanceCacheRepository.acquireLock(PERFORMANCE_LIST_LOCK_KEY, PERFORMANCE_LIST_LOCK_TTL);

		if (!locked) {
			return handleLockMiss();
		}

		log.info("공연 목록 캐시 miss - key={}", PERFORMANCE_LIST_KEY);

		try {
			List<PerformanceResponse> responses = performanceQueryMapper.findAll().stream()
				.map(this::toResponse)
				.toList();

			String json = serializePerformanceList(responses);
			performanceCacheRepository.setPerformanceList(PERFORMANCE_LIST_KEY, json, PERFORMANCE_LIST_TTL);

			log.info("공연 목록 캐시 저장 - key={}, ttl={}s", PERFORMANCE_LIST_KEY, PERFORMANCE_LIST_TTL.getSeconds());

			return responses;
		}finally {
			performanceCacheRepository.releaseLock(PERFORMANCE_LIST_LOCK_KEY);
		}

	}

	public PerformanceResponse getPerformance(Long performanceId) {
		return performanceQueryMapper.findById(performanceId)
			.map(this::toResponse)
			.orElseThrow(() -> new ApiException(ErrorCode.PERFORMANCE_NOT_FOUND));
	}

	private PerformanceResponse toResponse(PerformanceRow row) {
		return new PerformanceResponse(
			row.id(),
			row.title(),
			row.venue(),
			row.totalSeats()
		);
	}

	private String serializePerformanceList(List<PerformanceResponse> responses) {
		try {
			return objectMapper.writeValueAsString(responses);
		} catch (Exception e) {
			throw new IllegalStateException("공연 목록 캐시 직렬화에 실패했습니다.", e);
		}
	}

	private List<PerformanceResponse> deserializePerformanceList(String json) {
		try {
			return objectMapper.readValue(
				json,
				new TypeReference<List<PerformanceResponse>>() {}
			);
		} catch (Exception e) {
			throw new IllegalStateException("공연 목록 캐시 역직렬화에 실패했습니다.", e);
		}
	}

	private List<PerformanceResponse> handleLockMiss() {
		Optional<String> cached = performanceCacheRepository.getPerformanceList(PERFORMANCE_LIST_KEY);

		if (cached.isPresent()) {
			log.info("공연 목록 캐시 재조회 hit - key={}", PERFORMANCE_LIST_KEY);
			return deserializePerformanceList(cached.get());
		}

		log.info("공연 목록 캐시 재조회 miss - key={}", PERFORMANCE_LIST_KEY);

		return performanceQueryMapper.findAll().stream()
			.map(this::toResponse)
			.toList();
	}
}
