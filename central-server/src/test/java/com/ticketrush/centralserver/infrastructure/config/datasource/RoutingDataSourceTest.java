package com.ticketrush.centralserver.infrastructure.config.datasource;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronizationManager;

class RoutingDataSourceTest {

	private static class TestRoutingDataSource extends RoutingDataSource {
		public Object currentLookupKey() {
			return determineCurrentLookupKey();
		}
	}

	@AfterEach
	void tearDown() {
		TransactionSynchronizationManager.clear();
	}

	@Test
	@DisplayName("readOnly 트랜잭션이면 slave를 선택한다")
	void readOnly_트랜잭션_slave() {

		TestRoutingDataSource routingDataSource = new TestRoutingDataSource();

		TransactionSynchronizationManager.setActualTransactionActive(true);
		TransactionSynchronizationManager.setCurrentTransactionReadOnly(true);

		Object lookupKey = routingDataSource.currentLookupKey();

		assertEquals(DataSourceType.SLAVE, lookupKey);
	}

	@Test
	@DisplayName("일반 트랜잭션이면 master를 선택한다")
	void 일반_트랜잭션_master() {

		TestRoutingDataSource routingDataSource = new TestRoutingDataSource();

		TransactionSynchronizationManager.setActualTransactionActive(true);
		TransactionSynchronizationManager.setCurrentTransactionReadOnly(false);

		Object lookupKey = routingDataSource.currentLookupKey();

		assertEquals(DataSourceType.MASTER, lookupKey);
	}

}
