package net.codeoftheday.uaa.email;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.user.UserLocaleResolver;
import net.codeoftheday.uaa.util.template.TemplateResolver;

@Service
public class EmailTemplateService {

	private static final String ACTIVATION_EMAIL_TEMPLATE = "mail/activation";

	private static final String PASSWORD_RESET_EMAIL_TEMPLATE = "mail/passwordReset";

	private final UaaProperties uaaProperties;
	private final UserLocaleResolver localeResolver;
	private final TemplateResolver templateResolver;

	@Autowired
	public EmailTemplateService(final UaaProperties uaaProperties, final UserLocaleResolver localeResolver,
			final TemplateResolver templateService) {
		this.uaaProperties = uaaProperties;
		this.localeResolver = localeResolver;
		this.templateResolver = templateService;
	}

	public String processActivationMail(final User user) {
		final Map<String, Object> context = new HashMap<>();
		context.put("user", user);
		context.put("base_url", normalizedBaseUrl());

		return templateResolver.process(ACTIVATION_EMAIL_TEMPLATE, context, localeResolver.getLocale(user), "html");
	}

	public String processPasswordResetMail(final User user) {
		final Map<String, Object> context = new HashMap<>();
		context.put("user", user);
		context.put("base_url", normalizedBaseUrl());

		return templateResolver.process(PASSWORD_RESET_EMAIL_TEMPLATE, context, localeResolver.getLocale(user), "html");
	}

	private String normalizedBaseUrl() {
		final String baseUrl = uaaProperties.getBaseUrl();
		return baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

}
