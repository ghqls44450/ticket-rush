package com.ticketrush.centralserver.interfaces.api.performance;

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
@Sql(statements = {
	"DELETE FROM performance",
	"INSERT INTO performance (title, description, venue, total_seats) VALUES ('테스트 공연', '설명', '올림픽홀', 500)",
	"INSERT INTO performance (title, description, venue, total_seats) VALUES ('두번째 공연', '설명', '체조경기장', 1000)"
})
class PerformanceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("공연 목록 조회 API가 정상 응답한다")
	void 공연_목록_조회_API가_정상_응답한다() throws Exception {
		mockMvc.perform(get("/api/v1/performances"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data[0].title").value("두번째 공연"))
			.andExpect(jsonPath("$.data[0].venue").value("체조경기장"))
			.andExpect(jsonPath("$.data[0].totalSeats").value(1000))
			.andExpect(jsonPath("$.data[1].title").value("테스트 공연"));
	}


}
