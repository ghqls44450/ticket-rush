package com.ticketrush.centralserver.infrastructure.persistence.mapper;

import java.util.Optional;

import org.apache.ibatis.annotations.Mapper;

import com.ticketrush.centralserver.application.payment.PaymentCreateCommand;

@Mapper
public interface PaymentQueryMapper {

	void insertPayment(PaymentCreateCommand command);

	Optional<Long> findIdByReservationId(Long reservationId);

}
