package net.codeoftheday.uaa.authority.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import net.codeoftheday.uaa.authority.AuthorityRepository;
import net.codeoftheday.uaa.domain.Authority;

@Component
public class AuthorityDeserializer extends JsonDeserializer<Authority> {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Override
	public Authority deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException {
		// If the token is the ID of an Authority resolve it by using the repository
		if (p.getCurrentToken().equals(JsonToken.VALUE_STRING)) {
			final String id = p.getText();

			if (id == null || id.isEmpty()) {
				return null;
			}

			return authorityRepository.findOne(id);
		}

		// Otherwise use the default context parser to read the object.
		return ctxt.readValue(p, Authority.class);
	}

}
