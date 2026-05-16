package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.ticketrush.centralserver.application.reservation.ReservationConfirmCommand;

@Mapper
public interface ReservationQueryMapper {

	void insertReservation(ReservationConfirmCommand command);

	Optional<Long> findIdBySeatId(Long seatId);

}
