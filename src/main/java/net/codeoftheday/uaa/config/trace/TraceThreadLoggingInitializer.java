package net.codeoftheday.uaa.config.trace;

import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.turbo.TurboFilter;
import ch.qos.logback.core.spi.FilterReply;

@Component
public class TraceThreadLoggingInitializer {

	private static org.slf4j.Logger LOGGER = LoggerFactory.getLogger(TraceThreadLoggingInitializer.class);

	@EventListener
	public void handleContextRefresh(final ContextRefreshedEvent event) {
		final LoggerContext loggerContext = ((Logger) LoggerFactory.getLogger("")).getLoggerContext();
		loggerContext.addTurboFilter(new TurboFilter() {
			@Override
			public FilterReply decide(final Marker marker, final Logger logger, final Level level, final String format,
					final Object[] params, final Throwable t) {
				return TraceThreadLoggingSupport.isTraceActive() ? FilterReply.ACCEPT : FilterReply.NEUTRAL;
			}
		});
		LOGGER.info("ThreadLogging support initialized");
	}
}
