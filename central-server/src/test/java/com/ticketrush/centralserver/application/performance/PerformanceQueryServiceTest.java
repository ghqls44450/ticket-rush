package com.ticketrush.centralserver.application.performance;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ticketrush.centralserver.infrastructure.cache.PerformanceCacheRepository;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.PerformanceQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.PerformanceRow;
import com.ticketrush.centralserver.interfaces.api.performance.dto.PerformanceResponse;

@ExtendWith(MockitoExtension.class)
class PerformanceQueryServiceTest {

	private static final String PERFORMANCE_LIST_KEY = "performance:list:v1";
	private static final String PERFORMANCE_LIST_LOCK_KEY = "performance:list:v1:lock";

	@Mock
	private PerformanceCacheRepository performanceCacheRepository;

	@Mock
	private PerformanceQueryMapper performanceQueryMapper;

	private ObjectMapper objectMapper = new ObjectMapper();

	private PerformanceQueryService performanceQueryService;

	@BeforeEach
	void setUp() {
		performanceQueryService = new PerformanceQueryService(
			objectMapper,
			performanceCacheRepository,
			performanceQueryMapper
		);
	}

	@Test
	@DisplayName("캐시 미스 시 DB에서 공연 목록을 조회하고 캐시에 저장한다")
	void 캐시_미스_시_DB_조회_후_캐시에_저장한다() {
		List<PerformanceRow> rows = List.of(
			new PerformanceRow(1L, "공연 A", "올림픽홀", 500),
			new PerformanceRow(2L, "공연 B", "세종문화회관", 300)
		);

		when(performanceCacheRepository.getPerformanceList(PERFORMANCE_LIST_KEY))
			.thenReturn(Optional.empty());
		when(performanceQueryMapper.findAll())
			.thenReturn(rows);
		when(performanceCacheRepository.acquireLock(PERFORMANCE_LIST_LOCK_KEY, Duration.ofSeconds(60)))
			.thenReturn(true);

		List<PerformanceResponse> result = performanceQueryService.getPerformances();

		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).id());
		assertEquals("공연 A", result.get(0).title());
		assertEquals("올림픽홀", result.get(0).venue());
		assertEquals(500, result.get(0).totalSeats());

		assertEquals(2L, result.get(1).id());
		assertEquals("공연 B", result.get(1).title());
		assertEquals("세종문화회관", result.get(1).venue());
		assertEquals(300, result.get(1).totalSeats());

		verify(performanceCacheRepository, times(1))
			.getPerformanceList(PERFORMANCE_LIST_KEY);
		verify(performanceQueryMapper, times(1))
			.findAll();
		verify(performanceCacheRepository, times(1))
			.setPerformanceList(eq(PERFORMANCE_LIST_KEY), any(String.class), eq(Duration.ofSeconds(60)));
		verify(performanceCacheRepository, times(1))
			.acquireLock(PERFORMANCE_LIST_LOCK_KEY, Duration.ofSeconds(60));
		verify(performanceCacheRepository, times(1))
			.releaseLock(PERFORMANCE_LIST_LOCK_KEY);

	}

	@Test
	@DisplayName("캐시 히트 시 Redis 값을 반환하고 DB를 조회하지 않는다")
	void 캐시_히트_시_캐시값을_반환하고_DB를_조회하지_않는다() {

		String cachedJson = """
		[
		  {
			"id": 1,
			"title": "공연 A",
			"venue": "올림픽홀",
			"totalSeats": 500
		  },
		  {
			"id": 2,
			"title": "공연 B",
			"venue": "세종문화회관",
			"totalSeats": 300
		  }
		]
		""";

		when(performanceCacheRepository.getPerformanceList(PERFORMANCE_LIST_KEY))
			.thenReturn(Optional.of(cachedJson));

		List<PerformanceResponse> result = performanceQueryService.getPerformances();

		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).id());
		assertEquals("공연 A", result.get(0).title());
		assertEquals("올림픽홀", result.get(0).venue());
		assertEquals(500, result.get(0).totalSeats());

		assertEquals(2L, result.get(1).id());
		assertEquals("공연 B", result.get(1).title());
		assertEquals("세종문화회관", result.get(1).venue());
		assertEquals(300, result.get(1).totalSeats());

		verify(performanceCacheRepository, times(1))
			.getPerformanceList(PERFORMANCE_LIST_KEY);
		verify(performanceQueryMapper, times(0))
			.findAll();
		verify(performanceCacheRepository, times(0))
			.setPerformanceList(eq(PERFORMANCE_LIST_KEY), any(String.class), eq(Duration.ofSeconds(60)));
	}

	@Test
	@DisplayName("락 획득 실패 후 캐시 재조회 히트면 캐시 값을 반환한다")
	void 락_획득_실패_후_캐시_재조회_히트면_캐시값을_반환한다(){

		String cachedJson = """
		[
		  {
			"id": 1,
			"title": "공연 A",
			"venue": "올림픽홀",
			"totalSeats": 500
		  },
		  {
			"id": 2,
			"title": "공연 B",
			"venue": "세종문화회관",
			"totalSeats": 300
		  }
		]
		""";

		when(performanceCacheRepository.getPerformanceList(PERFORMANCE_LIST_KEY))
			.thenReturn(Optional.empty(), Optional.of(cachedJson));
		when(performanceCacheRepository.acquireLock(PERFORMANCE_LIST_LOCK_KEY, Duration.ofSeconds(60)))
			.thenReturn(false);

		List<PerformanceResponse> result = performanceQueryService.getPerformances();
		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).id());
		assertEquals("공연 A", result.get(0).title());
		assertEquals("올림픽홀", result.get(0).venue());
		assertEquals(500, result.get(0).totalSeats());

		assertEquals(2L, result.get(1).id());
		assertEquals("공연 B", result.get(1).title());
		assertEquals("세종문화회관", result.get(1).venue());
		assertEquals(300, result.get(1).totalSeats());

		verify(performanceCacheRepository, times(2))
			.getPerformanceList(PERFORMANCE_LIST_KEY);
		verify(performanceCacheRepository, times(1))
			.acquireLock(PERFORMANCE_LIST_LOCK_KEY, Duration.ofSeconds(60));
		verify(performanceQueryMapper, times(0))
			.findAll();
		verify(performanceCacheRepository, times(0))
			.setPerformanceList(eq(PERFORMANCE_LIST_KEY), any(String.class), eq(Duration.ofSeconds(60)));
	}

	@Test
	@DisplayName("락 획득 실패 후 캐시 재조회 미스면 DB를 조회한다")
	void 락_획득_실패_후_캐시_재조회_미스면_DB를_조회한다() {
		List<PerformanceRow> rows = List.of(
			new PerformanceRow(1L, "공연 A", "올림픽홀", 500),
			new PerformanceRow(2L, "공연 B", "세종문화회관", 300)
		);

		when(performanceCacheRepository.getPerformanceList(PERFORMANCE_LIST_KEY))
			.thenReturn(Optional.empty(), Optional.empty());

		when(performanceCacheRepository.acquireLock(PERFORMANCE_LIST_LOCK_KEY, Duration.ofSeconds(60)))
			.thenReturn(false);

		when(performanceQueryMapper.findAll())
			.thenReturn(rows);

		List<PerformanceResponse> result = performanceQueryService.getPerformances();

		assertEquals(2, result.size());
		assertEquals(1L, result.get(0).id());
		assertEquals("공연 A", result.get(0).title());
		assertEquals("올림픽홀", result.get(0).venue());
		assertEquals(500, result.get(0).totalSeats());

		assertEquals(2L, result.get(1).id());
		assertEquals("공연 B", result.get(1).title());
		assertEquals("세종문화회관", result.get(1).venue());
		assertEquals(300, result.get(1).totalSeats());

		verify(performanceCacheRepository, times(2))
			.getPerformanceList(PERFORMANCE_LIST_KEY);
		verify(performanceCacheRepository, times(1))
			.acquireLock(PERFORMANCE_LIST_LOCK_KEY, Duration.ofSeconds(60));
		verify(performanceQueryMapper, times(1))
			.findAll();
		verify(performanceCacheRepository, times(0))
			.setPerformanceList(eq(PERFORMANCE_LIST_KEY), any(String.class), eq(Duration.ofSeconds(60)));
	}

}