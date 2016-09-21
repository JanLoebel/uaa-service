package net.codeoftheday.uaa.email;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.apache.commons.codec.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.config.UaaProperties;

@Component
public class EmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	private final UaaProperties uaaProperties;
	private final JavaMailSender javaMailSender;

	@Autowired
	public EmailService(final UaaProperties uaaProperties, final JavaMailSender javaMailSender) {
		this.uaaProperties = uaaProperties;
		this.javaMailSender = javaMailSender;
	}

	@Async
	public void sendEmail(final String to, final String subject, final String content, final boolean isMultipart,
			final boolean isHtml) {

		final MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, isMultipart, CharEncoding.UTF_8);
			message.setFrom(uaaProperties.getMail().getFrom());
			message.setTo(to);
			message.setSubject(subject);
			message.setText(content, isHtml);
			javaMailSender.send(mimeMessage);
			LOGGER.debug("Sent email to User '{}' with subject '{}'", to, subject);
		} catch (final MessagingException | MailException e) {
			LOGGER.warn("Mail could not be sent to user '{}'", to, e);
		}
	}
}
