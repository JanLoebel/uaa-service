package net.codeoftheday.uaa.util.deserializer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

@Component
public class StringSetDeserializer extends JsonDeserializer<Set<String>> {

	@SuppressWarnings("unchecked")
	@Override
	public Set<String> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {

		// Directly one string value
		if (p.getCurrentToken().equals(JsonToken.VALUE_STRING)) {
			return new HashSet<>(Arrays.asList(p.getValueAsString()));
		}

		return ctxt.readValue(p, HashSet.class);
	}

}
