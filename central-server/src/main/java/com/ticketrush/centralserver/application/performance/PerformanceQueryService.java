package com.ticketrush.centralserver.application.performance;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketrush.centralserver.infrastructure.persistence.mapper.PerformanceQueryMapper;
import com.ticketrush.centralserver.infrastructure.persistence.model.PerformanceRow;
import com.ticketrush.centralserver.interfaces.api.performance.dto.PerformanceResponse;
import com.ticketrush.centralserver.support.exception.ApiException;
import com.ticketrush.centralserver.support.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PerformanceQueryService {

	private final PerformanceQueryMapper performanceQueryMapper;

	public List<PerformanceResponse> getPerformances() {
		return performanceQueryMapper.findAll().stream()
			.map(this::toResponse)
			.toList();
	}

	public PerformanceResponse getPerformance(Long performanceId) {
		return performanceQueryMapper.findById(performanceId)
			.map(this::toResponse)
			.orElseThrow(() -> new ApiException(ErrorCode.PERFORMANCE_NOT_FOUND));
	}

	private PerformanceResponse toResponse(PerformanceRow row) {
		return new PerformanceResponse(
			row.id(),
			row.title(),
			row.venue(),
			row.totalSeats()
		);
	}



}
