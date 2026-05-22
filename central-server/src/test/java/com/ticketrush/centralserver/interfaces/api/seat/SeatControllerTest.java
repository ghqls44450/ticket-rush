package com.ticketrush.centralserver.interfaces.api.seat;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
	"/sql/cleanup.sql",
	"/sql/seat-controller-test-data.sql"
})
class SeatControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("AVAILABLE 상태의 좌석을 HELD 상태로 점유할 수 있다")
	void available_좌석_점유_성공() throws Exception{
		mockMvc.perform(post("/api/v1/seats/1/hold"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.seatId").value(1))
			.andExpect(jsonPath("$.data.status").value("HELD"));
	}

	@Test
	@DisplayName("HELD 상태의 좌석은 다시 점유할 수 없다")
	void held_좌석_점유_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/2/hold"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_HELD"))
			.andExpect(jsonPath("$.error.message").value("점유할 수 없는 좌석입니다."));
	}

	@Test
	@DisplayName("CONFIRMED 상태의 좌석은 점유할 수 없다")
	void confirmed_좌석_점유_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/3/hold"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_HELD"))
			.andExpect(jsonPath("$.error.message").value("점유할 수 없는 좌석입니다."));
	}

	@Test
	@DisplayName("CANCELLED 상태의 좌석은 점유할 수 없다")
	void cancelled_좌석_점유_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/4/hold"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_HELD"))
			.andExpect(jsonPath("$.error.message").value("점유할 수 없는 좌석입니다."));
	}

	@Test
	@DisplayName("존재하지 않는 좌석은 점유할 수 없다")
	void 존재하지_않는_좌석_점유_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/9999/hold"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_NOT_FOUND"))
			.andExpect(jsonPath("$.error.message").value("좌석을 찾을 수 없습니다."));
	}

	@Test
	@DisplayName("잘못된 seatId 타입 요청 시 공통 에러 응답을 반환한다")
	void 잘못된_seatId_타입_요청() throws Exception{
		mockMvc.perform(post("/api/v1/seats/abc/hold"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("COMMON_INVALID_INPUT"))
			.andExpect(jsonPath("$.error.message").value("잘못된 요청입니다."));
	}

	@Test
	@DisplayName("동일 좌석에 동시에 점유 요청이 들어오면 하나만 성공한다")
	void 동일_좌석_동시_점유_요청_하나만_성공() throws Exception{

		int threadCount = 10;

		ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
		CountDownLatch startLatch = new CountDownLatch(1);
		CountDownLatch doneLatch = new CountDownLatch(threadCount);

		AtomicInteger successCount = new AtomicInteger();
		AtomicInteger failureCount = new AtomicInteger();

		for (int i = 0; i < threadCount; i++) {
			executorService.submit(() -> {
				try {
					startLatch.await();

					int status = mockMvc.perform(post("/api/v1/seats/1/hold"))
						.andReturn()
						.getResponse()
						.getStatus();

					if (status == 200) {
						successCount.incrementAndGet();
					} else {
						failureCount.incrementAndGet();
					}
				} catch (Exception e) {
					throw new RuntimeException(e);
				} finally {
					doneLatch.countDown();
				}
			});
		}
		startLatch.countDown();

		boolean finished = doneLatch.await(5, TimeUnit.SECONDS);

		executorService.shutdown();

		assertTrue(finished);
		assertEquals(1, successCount.get());
		assertEquals(9, failureCount.get());
	}

	@Test
	@DisplayName("HELD 상태의 좌석을 AVAILABLE 상태로 해제할 수 있다")
	void held_좌석_해제_성공() throws Exception{
		mockMvc.perform(post("/api/v1/seats/2/release"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.seatId").value(2))
			.andExpect(jsonPath("$.data.status").value("AVAILABLE"));
	}

	@Test
	@DisplayName("AVAILABLE 상태의 좌석은 해제할 수 없다")
	void available_좌석_해제_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/1/release"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_RELEASED"))
			.andExpect(jsonPath("$.error.message").value("해제할 수 없는 좌석입니다."));
	}

	@Test
	@DisplayName("CONFIRMED 상태의 좌석은 해제할 수 없다")
	void confirmed_좌석_해제_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/3/release"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_RELEASED"))
			.andExpect(jsonPath("$.error.message").value("해제할 수 없는 좌석입니다."));
	}

	@Test
	@DisplayName("CANCELLED 상태의 좌석은 해제할 수 없다")
	void cancelled_좌석_해제_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/4/release"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_RELEASED"))
			.andExpect(jsonPath("$.error.message").value("해제할 수 없는 좌석입니다."));
	}

	@Test
	@DisplayName("존재하지 않는 좌석은 해제할 수 없다")
	void 존재하지_않는_좌석_해제_실패() throws Exception{
		mockMvc.perform(post("/api/v1/seats/9999/release"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_NOT_FOUND"))
			.andExpect(jsonPath("$.error.message").value("좌석을 찾을 수 없습니다."));
	}

}