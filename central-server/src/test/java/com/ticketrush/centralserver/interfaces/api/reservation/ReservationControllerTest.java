package com.ticketrush.centralserver.interfaces.api.reservation;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = {
	"/sql/cleanup.sql",
	"/sql/reservation-controller-test-data.sql"
})
class ReservationControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("HELD 좌석을 예매하면 확정 성공한다")
	void HELD_좌석_예매_확정_성공() throws Exception {
		mockMvc.perform(post("/api/v1/reservations/confirm")
				.contentType("application/json")
				.content("""
					{
					  "userId": 1,
					  "seatId": 1
					}
				"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.paymentId").value(1))
			.andExpect(jsonPath("$.data.seatId").value(1))
			.andExpect(jsonPath("$.data.userId").value(1))
			.andExpect(jsonPath("$.data.reservationStatus").value("CONFIRMED"))
			.andExpect(jsonPath("$.data.paymentStatus").value("PENDING"))
			.andExpect(jsonPath("$.data.amount").value(50000));
		assertEquals("CONFIRMED", seatStatus(1L));
		assertEquals(1, reservationCount(1L));
		assertEquals(1, paymentCount(1L));
		assertEquals("CONFIRMED", reservationStatus(1L));
		assertEquals("PENDING", paymentStatus(1L));
	}

	@Test
	@DisplayName("존재하지 않는 좌석은 예매 실패한다")
	void 존재하지_안는_좌석_예매_실패() throws Exception {
		mockMvc.perform(post("/api/v1/reservations/confirm")
				.contentType("application/json")
				.content("""
					{
					  "userId": 1,
					  "seatId": 4
					}
				"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_NOT_FOUND"))
			.andExpect(jsonPath("$.error.message").value("좌석을 찾을 수 없습니다."));

		assertEquals(0, reservationCount(4L));
		assertEquals(0, paymentCount(4L));
	}

	@Test
	@DisplayName("AVAILABLE 상태의 좌석은 예매 실패한다")
	void AVAILABLE_상태의_좌석_예매_실패() throws Exception {
		mockMvc.perform(post("/api/v1/reservations/confirm")
				.contentType("application/json")
				.content("""
					{
					  "userId": 1,
					  "seatId": 2
					}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_CONFIRMED"))
			.andExpect(jsonPath("$.error.message").value("확정할 수 없는 좌석입니다."));

		assertEquals("AVAILABLE", seatStatus(2L));
		assertEquals(0, reservationCount(2L));
		assertEquals(0, paymentCount(2L));
	}

	@Test
	@DisplayName("CONFIRMED 상태의 좌석은 예매 실패한다")
	void CONFIRMED_상태의_좌석_예매_실패() throws Exception {
		mockMvc.perform(post("/api/v1/reservations/confirm")
				.contentType("application/json")
				.content("""
					{
					  "userId": 1,
					  "seatId": 3
					}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("SEAT_CANNOT_BE_CONFIRMED"))
			.andExpect(jsonPath("$.error.message").value("확정할 수 없는 좌석입니다."));

		assertEquals("CONFIRMED", seatStatus(3L));
		assertEquals(0, reservationCount(3L));
		assertEquals(0, paymentCount(3L));
	}

	@Test
	@DisplayName("잘못된 userId 값 검증 요청 시 공통 에러 응답을 반환한다")
	void 잘못된_userId_값_요청() throws Exception {
		mockMvc.perform(post("/api/v1/reservations/confirm")
				.contentType("application/json")
				.content("""
					{
					  "userId": null,
					  "seatId": 1
					}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("COMMON_INVALID_INPUT"))
			.andExpect(jsonPath("$.error.message").value("must not be null"));
	}

	@Test
	@DisplayName("잘못된 seatId 값 검증 요청 시 공통 에러 응답을 반환한다")
	void 잘못된_seatId_값_요청() throws Exception {
		mockMvc.perform(post("/api/v1/reservations/confirm")
				.contentType("application/json")
				.content("""
					{
					  "userId": 1,
					  "seatId": 0
					}
				"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("COMMON_INVALID_INPUT"))
			.andExpect(jsonPath("$.error.message").value("must be greater than 0"));
	}

	private String seatStatus(Long seatId) {
		return jdbcTemplate.queryForObject(
			"SELECT status FROM seat WHERE id = ?",
			String.class,
			seatId
		);
	}

	private int reservationCount(Long seatId) {
		Integer count = jdbcTemplate.queryForObject(
			"SELECT COUNT(*) FROM reservation WHERE seat_id = ?",
			Integer.class,
			seatId
		);
		return count == null ? 0 : count;
	}

	private int paymentCount(Long seatId) {
		Integer count = jdbcTemplate.queryForObject(
			"""
			SELECT COUNT(*)
			FROM payment p
			JOIN reservation r ON p.reservation_id = r.id
			WHERE r.seat_id = ?
			""",
			Integer.class,
			seatId
		);
		return count == null ? 0 : count;
	}

	private String reservationStatus(Long seatId) {
		return jdbcTemplate.queryForObject(
			"SELECT status FROM reservation WHERE seat_id = ?",
			String.class,
			seatId
		);
	}

	private String paymentStatus(Long seatId) {
		return jdbcTemplate.queryForObject(
			"""
			SELECT p.status
			FROM payment p
			JOIN reservation r ON p.reservation_id = r.id
			WHERE r.seat_id = ?
			""",
			String.class,
			seatId
		);
	}

}
