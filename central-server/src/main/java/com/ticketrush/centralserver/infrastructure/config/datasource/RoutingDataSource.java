package com.ticketrush.centralserver.infrastructure.config.datasource;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.support.TransactionSynchronizationManager;

public class RoutingDataSource extends AbstractRoutingDataSource {

	@Override
	protected Object determineCurrentLookupKey() {

		if (RoutingDataSourceContext.isForceMaster()) {
			return DataSourceType.MASTER;
		}

		boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();

		if (readOnly) {
			return DataSourceType.SLAVE;
		}

		return DataSourceType.MASTER;
	}

}
