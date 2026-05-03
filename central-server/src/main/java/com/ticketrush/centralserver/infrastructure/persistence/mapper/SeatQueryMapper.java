package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;

@Mapper
public interface SeatQueryMapper {

	List<SeatRow> findByScheduleIdAndStatus(@Param("scheduleId") Long scheduleId, @Param("status") String status);

}
