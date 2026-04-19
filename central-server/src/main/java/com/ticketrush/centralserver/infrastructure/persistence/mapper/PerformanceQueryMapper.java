package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ticketrush.centralserver.infrastructure.persistence.model.PerformanceRow;

@Mapper
public interface PerformanceQueryMapper {

	List<PerformanceRow> findAll();

}
