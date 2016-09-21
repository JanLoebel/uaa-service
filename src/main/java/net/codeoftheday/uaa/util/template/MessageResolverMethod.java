package net.codeoftheday.uaa.util.template;

import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;

import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

public class MessageResolverMethod implements TemplateMethodModelEx {

	private final MessageSource messageSource;
	private final Locale locale;

	public MessageResolverMethod(final MessageSource messageSource, final Locale locale) {
		this.messageSource = messageSource;
		this.locale = locale;
	}

	@Override
	public Object exec(@SuppressWarnings("rawtypes") final List arguments) throws TemplateModelException {
		if (arguments.isEmpty()) {
			throw new TemplateModelException("Need at least one argument.");
		}

		final String code = FreemarkerUtils.convertToString(arguments.get(0));
		if (code == null || code.isEmpty()) {
			throw new TemplateModelException("Invalid code value '" + code + "'");
		}

		if (arguments.size() == 1) {
			// Only one parameter given so just return the message
			return messageSource.getMessage(code, null, locale);
		}

		final Object[] extractArray = FreemarkerUtils.extractArray(arguments);
		return messageSource.getMessage(code, extractArray, locale);
	}

}
