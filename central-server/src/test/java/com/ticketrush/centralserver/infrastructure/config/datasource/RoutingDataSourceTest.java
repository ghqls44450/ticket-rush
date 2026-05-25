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
		RoutingDataSourceContext.clear();
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

	@Test
	@DisplayName("master 강제 설정이 있으면 readOnly 트랜잭션도 master를 선택한다")
	void master_강제_설정_readOnly_트랜잭션_master_선택() {
		TestRoutingDataSource routingDataSource = new TestRoutingDataSource();

		TransactionSynchronizationManager.setActualTransactionActive(true);
		TransactionSynchronizationManager.setCurrentTransactionReadOnly(true);
		RoutingDataSourceContext.forceMaster();

		Object lookupKey = routingDataSource.currentLookupKey();
		assertEquals(DataSourceType.MASTER, lookupKey);
	}

	@Test
	@DisplayName("master 강제 설정을 해제하면 다시 기본 라우팅 규칙을 따른다")
	void master_강제_설정_해제_다시_기본_라우팅_규칙() {
		TestRoutingDataSource routingDataSource = new TestRoutingDataSource();

		TransactionSynchronizationManager.setActualTransactionActive(true);
		TransactionSynchronizationManager.setCurrentTransactionReadOnly(true);
		RoutingDataSourceContext.forceMaster();
		RoutingDataSourceContext.clear();

		Object lookupKey = routingDataSource.currentLookupKey();

		assertEquals(DataSourceType.SLAVE, lookupKey);
	}
}
