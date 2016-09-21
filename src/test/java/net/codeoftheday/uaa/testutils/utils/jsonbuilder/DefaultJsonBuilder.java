package net.codeoftheday.uaa.testutils.utils.jsonbuilder;

import java.util.HashMap;
import java.util.Map;

public class DefaultJsonBuilder implements JsonBuilder {

	private final Map<String, Object> json = new HashMap<>();

	public static DefaultJsonBuilder jsonBuilder() {
		return new DefaultJsonBuilder();
	}

	@Override
	public DefaultJsonBuilder put(final String key, final Object value) {
		json.put(key, value);
		return this;
	}

	@Override
	public Map<String, Object> build() {
		return json;
	}

	@Override
	public JsonBuilder newObject(final String key) {
		return new ObjectJsonBuilder(this, key);
	}

	@Override
	public JsonBuilder buildObject() {
		throw new IllegalStateException("This should not be called on the root builder, use build()!");
	}

	private static class ObjectJsonBuilder implements JsonBuilder {
		private final Map<String, Object> innerJson = new HashMap<>();

		private final String key;
		private final JsonBuilder builder;

		ObjectJsonBuilder(final JsonBuilder builder, final String key) {
			this.builder = builder;
			this.key = key;
		}

		@Override
		public JsonBuilder put(final String key, final Object value) {
			innerJson.put(key, value);
			return this;
		}

		@Override
		public JsonBuilder newObject(final String key) {
			return new ObjectJsonBuilder(this, key);
		}

		@Override
		public Map<String, Object> build() {
			throw new IllegalStateException("This should not be called on the object builder, use buildObject()!");
		}

		@Override
		public JsonBuilder buildObject() {
			builder.put(key, innerJson);
			return builder;
		}
	}

}
