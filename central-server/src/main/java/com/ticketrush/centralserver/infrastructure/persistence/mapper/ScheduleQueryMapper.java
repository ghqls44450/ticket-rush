package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.ticketrush.centralserver.infrastructure.persistence.model.ScheduleRow;

@Mapper
public interface ScheduleQueryMapper {

	List<ScheduleRow> findByPerformanceId(Long performanceId);

}
