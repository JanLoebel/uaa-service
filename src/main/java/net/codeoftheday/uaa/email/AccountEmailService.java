package net.codeoftheday.uaa.email;

import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.user.UserLocaleResolver;

@Service
public class AccountEmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountEmailService.class);

	@Autowired
	private EmailService emailService;

	@Autowired
	private MessageSource messageSource;

	@Autowired
	private EmailTemplateService templateService;

	@Autowired
	private UserLocaleResolver localeResolver;

	public void sendActivationCodeMail(final User createdUser) {
		if (StringUtils.isBlank(createdUser.getActivationCode())) {
			LOGGER.debug("No verification code to send for user: {}", createdUser.getUsername());
			return;
		}

		// Use template service to read content for the activation email.
		final String content = templateService.processActivationMail(createdUser);
		final String title = getMessage("account.activation.title", createdUser);

		emailService.sendEmail(createdUser.getEmail(), title, content, false, true);
	}

	public void sendPasswordResetMail(final User user) {
		final String content = templateService.processPasswordResetMail(user);
		final String title = getMessage("account.passwordreset.title", user);

		emailService.sendEmail(user.getEmail(), title, content, false, true);
	}

	private String getMessage(final String key, final User user) {
		return messageSource.getMessage(key, null, parseLocale(user));
	}

	private Locale parseLocale(final User user) {
		return localeResolver.getLocale(user);
	}

}
