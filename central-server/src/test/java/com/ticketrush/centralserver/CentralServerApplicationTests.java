package com.ticketrush.centralserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.ticketrush.centralserver.application.performance.PerformanceQueryService;
import com.ticketrush.centralserver.application.schedule.ScheduleQueryService;

@SpringBootTest(
	properties = "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
)
@ActiveProfiles("test")
class CentralServerApplicationTests {

	@MockitoBean
	private PerformanceQueryService performanceQueryService;

	@MockitoBean
	private ScheduleQueryService scheduleQueryService;

	@Test
	@DisplayName("애플리케이션 컨텍스트가 정상적으로 로드된다")
	void 애플리케이션_컨텍스트가_정상적으로_로드된다() {
	}

}
