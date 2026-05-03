package com.ticketrush.centralserver.interfaces.api.schedule;

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
	"/sql/schedule-controller-test-data.sql"
})
class ScheduleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("특정 회차의 좌석을 조회할 수 있다.")
	void 특정_회차의_좌석을_조회할_수_있다() throws Exception {
		mockMvc.perform(get("/api/v1/schedules/1/seats"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.length()").value(3))
			.andExpect(jsonPath("$.data[0].seatNumber").value("A1"))
			.andExpect(jsonPath("$.data[1].seatNumber").value("A2"))
			.andExpect(jsonPath("$.data[2].seatNumber").value("A3"))
		;
	}

	@Test
	@DisplayName("좌석의 상태를 특정해 조회할 수 있다.")
	void 좌석의_상태를_특정해_조회할_수_있다() throws Exception {
		mockMvc.perform(get("/api/v1/schedules/1/seats?status=AVAILABLE"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(1))
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data[0].seatNumber").value("A1"))
			.andExpect(jsonPath("$.data[0].status").value("AVAILABLE"))
		;
	}

	@Test
	@DisplayName("두번째 회차의 좌석을 특정해 조회할 수 있다.")
	void 두번째_회차의_좌석을_특정해_조회할_수_있다() throws Exception {
		mockMvc.perform(get("/api/v1/schedules/2/seats"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.length()").value(1))
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data[0].seatNumber").value("B1"))
			.andExpect(jsonPath("$.data[0].status").value("AVAILABLE"))
		;
	}
}
