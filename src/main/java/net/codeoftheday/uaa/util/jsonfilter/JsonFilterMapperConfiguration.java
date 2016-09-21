package net.codeoftheday.uaa.util.jsonfilter;

import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@Configuration
public class JsonFilterMapperConfiguration {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired(required = false)
	private List<AbstractJsonFilter> jsonFilters;

	@PostConstruct
	public void addRolesFilter() {
		if (jsonFilters == null || jsonFilters.isEmpty()) {
			return;
		}

		// Register filter
		final SimpleFilterProvider filterProvider = new SimpleFilterProvider();
		for (final AbstractJsonFilter jsonFilter : jsonFilters) {
			filterProvider.addFilter(jsonFilter.getFilterIdentifier(), jsonFilter);
		}
		objectMapper.setConfig(objectMapper.getSerializationConfig().withFilters(filterProvider));
	}
}
