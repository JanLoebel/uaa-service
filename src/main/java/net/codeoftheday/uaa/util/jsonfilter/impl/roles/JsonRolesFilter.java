package net.codeoftheday.uaa.util.jsonfilter.impl.roles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.PropertyWriter;

import net.codeoftheday.uaa.util.SecurityUtils;
import net.codeoftheday.uaa.util.jsonfilter.AbstractJsonFilter;

@Component
public class JsonRolesFilter extends AbstractJsonFilter {

	private static final String JSON_FILTER_IDENTIFIER = "roles";
	private static final Logger LOGGER = LoggerFactory.getLogger(JsonRolesFilter.class);

	@Override
	public void serializeAsField(final Object pojo, final JsonGenerator jgen, final SerializerProvider provider,
			final PropertyWriter writer) throws Exception {

		// First check JsonIgnoreRoles
		final JsonIgnoreRoles jsonIgnoreRoles = writer.getAnnotation(JsonIgnoreRoles.class);
		if (jsonIgnoreRoles != null) {
			final boolean serialize = !authorityContains(jsonIgnoreRoles.roles());
			LOGGER.debug("Field: {} has JsonIgnoreRoles-Annotation -> serialize = {}", writer.getName(), serialize);
			if (serialize) {
				super.serializeAsField(pojo, jgen, provider, writer);
			}
			return;
		}

		// Then check JsonVisibleRoles
		final JsonVisibleRoles jsonVisibleRoles = writer.getAnnotation(JsonVisibleRoles.class);
		if (jsonVisibleRoles != null) {
			final boolean serialize = authorityContains(jsonVisibleRoles.roles());
			LOGGER.debug("Field: {} has JsonVisibleRoles-Annotation -> serialize = {}", writer.getName(), serialize);
			if (serialize) {
				super.serializeAsField(pojo, jgen, provider, writer);
			}
			return;
		}

		LOGGER.trace("Field: {} has None-JsonRoles-Annotation -> serialize = true", writer.getName());
		super.serializeAsField(pojo, jgen, provider, writer);
	}

	private boolean authorityContains(final String allowedRoles) {
		if (allowedRoles != null) {
			return SecurityUtils.hasAnyAuthority(allowedRoles.split(","));
		}
		return false;
	}

	@Override
	protected String getFilterIdentifier() {
		return JSON_FILTER_IDENTIFIER;
	}
}
