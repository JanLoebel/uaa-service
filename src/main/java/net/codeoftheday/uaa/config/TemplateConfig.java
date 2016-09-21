package net.codeoftheday.uaa.config;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.ui.freemarker.SpringTemplateLoader;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import freemarker.cache.FileTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.TemplateException;

@Configuration
public class TemplateConfig {

	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateConfig.class);

	@Autowired
	private ResourceLoader resourceLoader;

	@Autowired
	private UaaProperties uaaProperties;

	@Bean
	public FreeMarkerConfigurer freemarkerConfig() throws IOException, TemplateException {
		final FreeMarkerConfigurationFactory factory = new FreeMarkerConfigurationFactory();

		// If overwritten use path of user
		if (isNotBlank(uaaProperties.getTemplatePath())) {
			final TemplateLoader templateLoader = getTemplateLoader(uaaProperties.getTemplatePath());
			factory.setPreTemplateLoaders(templateLoader);
		}

		// Default configurations
		factory.setPostTemplateLoaders(getTemplateLoader("classpath:/templates/"));
		factory.setDefaultEncoding("UTF-8");

		final FreeMarkerConfigurer result = new FreeMarkerConfigurer();
		result.setConfiguration(factory.createConfiguration());
		return result;
	}

	private TemplateLoader getTemplateLoader(final String templatePath) {
		try {
			// Classpath loading
			if (templatePath.startsWith("classpath:")) {
				final Resource path = resourceLoader.getResource(templatePath);
				return new FileTemplateLoader(path.getFile());
			}

			// Filebase loading
			return new FileTemplateLoader(new File(templatePath));
		} catch (final IOException e) {
			LOGGER.debug("Error while getting templateLoader, will fallback to SpringTemplateLoader.", e);
			return new SpringTemplateLoader(resourceLoader, templatePath);
		}
	}

}
