package com.ticketrush.centralserver.application.seat;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;

import com.ticketrush.centralserver.infrastructure.cache.SeatHoldCacheRepository;
import com.ticketrush.centralserver.infrastructure.persistence.mapper.SeatQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;

@SpringBootTest
@ActiveProfiles("test")
@Sql(scripts = {
	"/sql/cleanup.sql",
	"/sql/seat-controller-test-data.sql"
})
class SeatReleaseServiceTest {

	@Autowired
	private SeatReleaseService seatReleaseService;

	@Autowired
	private SeatQueryMapper seatQueryMapper;

	@MockitoBean
	private SeatHoldCacheRepository seatHoldCacheRepository;


	@Test
	@DisplayName("만료된 좌석을 복구할 수 있다")
	void 만료된_좌석_복구_성공() {
		LocalDateTime threshold = LocalDateTime.of(2026, 5, 10, 18, 50, 0);

		int releasedCount = seatReleaseService.releaseExpiredSeats(threshold);

		SeatRow expiredSeat = seatQueryMapper.findById(2L).orElseThrow();
		SeatRow recentSeat = seatQueryMapper.findById(5L).orElseThrow();

		assertEquals(1, releasedCount);
		assertEquals("AVAILABLE", expiredSeat.status());
		assertEquals("HELD", recentSeat.status());

		verify(seatHoldCacheRepository, times(1))
			.deleteHold(2L);
	}

	@Test
	@DisplayName("만료된 좌석이 없으면 0건을 반환한다")
	void 만료된_좌석이_없으면_0건을_반환한다() {
		LocalDateTime threshold = LocalDateTime.of(2026, 5, 10, 18, 30, 0);

		int releasedCount = seatReleaseService.releaseExpiredSeats(threshold);

		SeatRow expiredCandidate = seatQueryMapper.findById(2L).orElseThrow();
		SeatRow recentSeat = seatQueryMapper.findById(5L).orElseThrow();

		assertEquals(0, releasedCount);
		assertEquals("HELD", expiredCandidate.status());
		assertEquals("HELD", recentSeat.status());
		verify(seatHoldCacheRepository, never())
			.deleteHold(anyLong());
	}


}
