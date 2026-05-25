package com.ticketrush.centralserver.infrastructure.config.datasource;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class DataSourceConfig {

	@Bean
	@ConfigurationProperties(prefix = "app.datasource.master")
	public DataSourceProperties masterDataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource masterDataSource() {
		return masterDataSourceProperties()
			.initializeDataSourceBuilder()
			.build();
	}

	@Bean
	@ConfigurationProperties(prefix = "app.datasource.slave1")
	public DataSourceProperties slave1DataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource slave1DataSource() {
		return slave1DataSourceProperties()
			.initializeDataSourceBuilder()
			.build();
	}

	@Bean
	@ConfigurationProperties(prefix = "app.datasource.slave2")
	public DataSourceProperties slave2DataSourceProperties() {
		return new DataSourceProperties();
	}

	@Bean
	public DataSource slave2DataSource() {
		return slave2DataSourceProperties()
			.initializeDataSourceBuilder()
			.build();
	}

	@Bean
	@Primary
	public DataSource routingDataSource() {
		RoutingDataSource routingDataSource = new RoutingDataSource();

		Map<Object, Object> targetDataSources = new HashMap<>();

		targetDataSources.put(DataSourceType.MASTER, masterDataSource());
		targetDataSources.put(DataSourceType.SLAVE, slave1DataSource());

		routingDataSource.setTargetDataSources(targetDataSources);
		routingDataSource.setDefaultTargetDataSource(masterDataSource());

		return routingDataSource;
	}

}
