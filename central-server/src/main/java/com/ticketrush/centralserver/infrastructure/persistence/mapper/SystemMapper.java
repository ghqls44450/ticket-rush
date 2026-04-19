package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SystemMapper {
	Integer selectOne();
}
