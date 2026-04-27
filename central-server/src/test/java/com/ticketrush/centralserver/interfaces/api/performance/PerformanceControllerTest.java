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
	"ALTER TABLE performance AUTO_INCREMENT = 1",
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

	@Test
	@DisplayName("공연 단건 조회 API가 정상 응답한다")
	void 공연_단건_조회_API가_정상_응답한다() throws Exception {
		mockMvc.perform(get("/api/v1/performances/{performanceId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.data.id").value(1))
			.andExpect(jsonPath("$.data.title").value("테스트 공연"))
			.andExpect(jsonPath("$.data.venue").value("올림픽홀"))
			.andExpect(jsonPath("$.data.totalSeats").value(500));
	}

	@Test
	@DisplayName("존재하지 않는 공연 조회 시 404 에러 응답을 반환한다")
	void 존재하지_않는_공연_조회_시_404_에러_응답을_반환한다() throws Exception {
		mockMvc.perform(get("/api/v1/performances/{performanceId}", 9999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.error.code").value("PERFORMANCE_NOT_FOUND"))
			.andExpect(jsonPath("$.error.message").value("공연을 찾을 수 없습니다."));
	}



}
