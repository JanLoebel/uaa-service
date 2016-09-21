package net.codeoftheday.uaa.util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

public class HttpHeaderInterceptor implements ClientHttpRequestInterceptor {

	private final Map<String, String> headers;

	public HttpHeaderInterceptor(final String key, final String value) {
		this(new HashMap<>());
		headers.put(key, value);
	}

	public HttpHeaderInterceptor() {
		this(new HashMap<>());
	}

	public HttpHeaderInterceptor(final Map<String, String> headers) {
		this.headers = headers;
	}

	public void addHeader(final String key, final String value) {
		this.headers.put(key, value);
	}

	@Override
	public ClientHttpResponse intercept(final HttpRequest request, final byte[] body,
			final ClientHttpRequestExecution execution) throws IOException {

		final HttpHeaders requestHeaders = request.getHeaders();

		for (final Entry<String, String> entry : headers.entrySet()) {
			requestHeaders.add(entry.getKey(), entry.getValue());
		}

		return execution.execute(request, body);
	}

}
