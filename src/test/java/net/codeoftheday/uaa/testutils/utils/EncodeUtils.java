package net.codeoftheday.uaa.testutils.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringJoiner;

import org.springframework.util.Base64Utils;

public final class EncodeUtils {

	private EncodeUtils() {
	}

	public static String encodeBase64(final String username, final String password) {
		return encodeBase64(username + ":" + password);
	}

	public static String encodeBase64(final String input) {
		return Base64Utils.encodeToString(input.getBytes(Charset.forName("UTF-8")));
	}

	public static String encodeUrl(final Map<String, Object> input) {
		final StringJoiner joiner = new StringJoiner("&");

		try {
			for (final Entry<String, Object> entry : input.entrySet()) {
				final String key = URLEncoder.encode(entry.getKey(), "UTF-8");
				final String value = URLEncoder.encode(entry.getValue().toString(), "UTF-8");
				joiner.add(key + "=" + value);
			}
		} catch (final UnsupportedEncodingException e) {
			throw new RuntimeException("Encoded is missing for URLEncoder!", e);
		}

		return joiner.toString();
	}

}
