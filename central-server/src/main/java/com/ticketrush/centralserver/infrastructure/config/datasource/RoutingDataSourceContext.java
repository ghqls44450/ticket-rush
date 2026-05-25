package com.ticketrush.centralserver.infrastructure.config.datasource;

public class RoutingDataSourceContext {

	private static final ThreadLocal<Boolean> FORCE_MASTER = ThreadLocal.withInitial(() -> false);

	private RoutingDataSourceContext() {
	}

	public static void forceMaster() {
		FORCE_MASTER.set(true);
	}

	public static boolean isForceMaster() {
		return FORCE_MASTER.get();
	}

	public static void clear() {
		FORCE_MASTER.remove();
	}
}
