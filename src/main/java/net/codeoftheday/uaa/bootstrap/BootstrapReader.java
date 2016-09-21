package net.codeoftheday.uaa.bootstrap;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BootstrapReader {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private ResourceLoader resourceLoader;

	public Optional<Bootstrap> readBootstrap(final String filePath) throws IOException {
		if (isBlank(filePath)) {
			return Optional.empty();
		}

		final Resource bootstrapFile = resourceLoader.getResource(filePath);

		if (bootstrapFile == null || !bootstrapFile.exists()) {
			return Optional.empty();
		}

		try (InputStreamReader inputStreamReader = new InputStreamReader(bootstrapFile.getInputStream())) {
			return Optional.of(objectMapper.readValue(inputStreamReader, Bootstrap.class));
		}
	}

}
