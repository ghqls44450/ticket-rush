package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.test.context.ActiveProfiles;

@MybatisTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class SystemMapperTest {

	private final SystemMapper systemMapper;

	@Autowired
	SystemMapperTest(SystemMapper systemMapper) {
		this.systemMapper = systemMapper;
	}

	@Test
	@DisplayName("MyBatis로 MySQL에 기본 조회를 수행할 수 있다")
	void MyBatis로_MySQL에_기본_조회를_수행할_수_있다() {
		Integer result = systemMapper.selectOne();

		assertThat(result).isEqualTo(1);
	}
}