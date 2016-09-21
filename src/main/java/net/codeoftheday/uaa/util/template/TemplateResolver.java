package net.codeoftheday.uaa.util.template;

import static java.util.Locale.ENGLISH;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateNotFoundException;
import net.codeoftheday.uaa.user.UserLocaleResolver;

@Component
public class TemplateResolver {
	private static final Logger LOGGER = LoggerFactory.getLogger(TemplateResolver.class);

	@Autowired
	private Configuration templateConfiguration;

	@Autowired
	private UserLocaleResolver userLocaleResolver;

	@Autowired
	private MessageSource messageSource;

	public String process(final String template, final Map<String, Object> context, final Locale locale,
			final String fileType) {
		try {
			return innerProcess(template, context, locale, fileType);
		} catch (@SuppressWarnings("squid:S1166") final TemplateNotFoundException tnfe) {
			// Exception will be ignored because it's expected
			LOGGER.debug("Template with given locale not found, next try with default locale.");
			return processDefaultLocale(template, context, fileType);
		}
	}

	private String processDefaultLocale(final String template, final Map<String, Object> context,
			final String fileType) {
		// Second try with default locale
		try {
			return innerProcess(template, context, userLocaleResolver.getDefaultLocale(), fileType);
		} catch (@SuppressWarnings("squid:S1166") final TemplateNotFoundException tnfe) {
			// Exception will be ignored because it's expected
			LOGGER.debug("Template with default locale not found, next try with fallback locale.");
			return processFallbackLocale(template, context, fileType);
		}
	}

	private String processFallbackLocale(final String template, final Map<String, Object> context,
			final String fileType) {
		try {
			// Third try with fallback locale
			return innerProcess(template, context, userLocaleResolver.getFallbackLocale(), fileType);
		} catch (final TemplateNotFoundException tnfe) {
			throw new RuntimeException("Template with fallback locale not found.", tnfe);
		}
	}

	private String innerProcess(final String template, final Map<String, Object> context, final Locale locale,
			final String filetype) throws TemplateNotFoundException {
		try {
			final String i18nTemplatePath = getI18nHtmlTemplate(locale, template, filetype);
			LOGGER.debug("Try to process template: '{}' with locale: '{}'", i18nTemplatePath,
					locale.getDisplayName(ENGLISH));

			final Template loadedTemplate = templateConfiguration.getTemplate(i18nTemplatePath);

			final Map<String, Object> enrichedContext = enrichtContextWithMessageSource(context, locale);
			return FreeMarkerTemplateUtils.processTemplateIntoString(loadedTemplate, enrichedContext);
		} catch (final TemplateNotFoundException tnfe) {
			// Will be handled by the outer process
			throw tnfe;
		} catch (final IOException | TemplateException e) {
			throw new RuntimeException(e);
		}
	}

	private Map<String, Object> enrichtContextWithMessageSource(final Map<String, Object> context,
			final Locale locale) {
		if (context.containsKey("msg")) {
			LOGGER.warn("Context has already a key with name 'msg', so skip enrichment.");
			return context;
		}

		// Add message source
		context.put("msg", new MessageResolverMethod(messageSource, locale));
		return context;
	}

	private String getI18nHtmlTemplate(final Locale locale, final String templateName, final String filetype) {
		// /template/ is automatically added by spring
		return templateName + "_" + locale.getLanguage() + "." + filetype;
	}

}
