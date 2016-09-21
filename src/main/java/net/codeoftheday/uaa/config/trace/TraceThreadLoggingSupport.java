package net.codeoftheday.uaa.config.trace;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class TraceThreadLoggingSupport {
	private static final Map<Long, Boolean> THREAD_TO_ENABLED = new HashMap<>();

	private TraceThreadLoggingSupport() {
	}

	public static void traceActive(final boolean enabled) {
		THREAD_TO_ENABLED.put(Thread.currentThread().getId(), enabled);
	}

	public static boolean isTraceActive() {
		return Optional.ofNullable(THREAD_TO_ENABLED.get(Thread.currentThread().getId())).orElse(false);
	}

	public static void cleanup() {
		THREAD_TO_ENABLED.remove(Thread.currentThread().getId());
	}
}
