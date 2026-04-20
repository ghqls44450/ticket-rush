package com.ticketrush.centralserver.application.performance;

import java.util.List;

import org.springframework.stereotype.Service;

import com.ticketrush.centralserver.infrastructure.persistence.mapper.PerformanceQueryMapper;
import com.ticketrush.centralserver.interfaces.api.performance.dto.PerformanceResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class PerformanceQueryService {

	private final PerformanceQueryMapper performanceQueryMapper;

	public List<PerformanceResponse> getPerformances() {
		return performanceQueryMapper.findAll().stream()
			.map(row -> new PerformanceResponse(
				row.id(),
				row.title(),
				row.venue(),
				row.totalSeats()
			))
			.toList();
	}

}
