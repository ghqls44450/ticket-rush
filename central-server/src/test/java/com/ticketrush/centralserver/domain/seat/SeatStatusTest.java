package com.ticketrush.centralserver.domain.seat;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SeatStatusTest {
	@Test
	@DisplayName("허용되는 좌석 상태 전이는 true를 반환한다")
	void 허용되는_상태_전이() {
		assertThat(SeatStatus.AVAILABLE.canTransitionTo(SeatStatus.HELD)).isTrue();
		assertThat(SeatStatus.HELD.canTransitionTo(SeatStatus.CONFIRMED)).isTrue();
		assertThat(SeatStatus.HELD.canTransitionTo(SeatStatus.AVAILABLE)).isTrue();
		assertThat(SeatStatus.CONFIRMED.canTransitionTo(SeatStatus.CANCELLED)).isTrue();
	}

	@Test
	@DisplayName("허용되지 않는 좌석 상태 전이는 false를 반환한다")
	void 허용되지_않는_상태_전이() {
		assertThat(SeatStatus.AVAILABLE.canTransitionTo(SeatStatus.CONFIRMED)).isFalse();
		assertThat(SeatStatus.AVAILABLE.canTransitionTo(SeatStatus.CANCELLED)).isFalse();
		assertThat(SeatStatus.CONFIRMED.canTransitionTo(SeatStatus.HELD)).isFalse();
		assertThat(SeatStatus.CANCELLED.canTransitionTo(SeatStatus.HELD)).isFalse();
		assertThat(SeatStatus.AVAILABLE.canTransitionTo(null)).isFalse();
	}
}