package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.ticketrush.centralserver.infrastructure.persistence.model.SeatRow;

@Mapper
public interface SeatQueryMapper {

	List<SeatRow> findByScheduleIdAndStatus(@Param("scheduleId") Long scheduleId, @Param("status") String status);

	Optional<SeatRow> findById(@Param("seatId") Long seatId);

	int holdSeat(@Param("seatId") Long seatId);

	Optional<SeatRow> findByIdForUpdate(@Param("seatId") Long seatId);

	int holdSeatIfAvailable(@Param("seatId") Long seatId);

}
