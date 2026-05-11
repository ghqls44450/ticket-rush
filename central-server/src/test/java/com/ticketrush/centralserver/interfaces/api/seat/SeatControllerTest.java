package com.ticketrush.centralserver.interfaces.api.seat;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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


}