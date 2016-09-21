package net.codeoftheday.uaa.config.trace;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

@Component
public class TraceThreadLoggingFilterBean extends GenericFilterBean {

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain)
			throws IOException, ServletException {
		try {
			final boolean logEverythingForThisRequest = "on".equalsIgnoreCase(request.getParameter("trace"));
			TraceThreadLoggingSupport.traceActive(logEverythingForThisRequest);
			chain.doFilter(request, response);
		} finally {
			TraceThreadLoggingSupport.cleanup();
		}
	}

}
