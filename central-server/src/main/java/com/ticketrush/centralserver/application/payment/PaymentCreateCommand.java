package com.ticketrush.centralserver.application.payment;

public record PaymentCreateCommand(
	Long reservationId,
	Integer amount,
	String status,
	String method
) {
}
