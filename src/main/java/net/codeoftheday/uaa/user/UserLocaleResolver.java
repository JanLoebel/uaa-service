package net.codeoftheday.uaa.user;

import static org.apache.commons.lang3.StringUtils.isBlank;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.domain.User;

@Component
public class UserLocaleResolver {

	@Autowired
	private UaaProperties uaaProperties;

	public String resolveLocale(final String localeToParse) {
		return localeToString(getLocale(localeToParse));
	}

	public Locale getLocale(final User user) {
		return getLocale(user.getLocale());
	}

	public Locale getLocale(final String locale) {
		if (isBlank(locale)) {
			return getDefaultLocale();
		}

		final Locale userLocale = lookupLocale(locale);
		if (userLocale != null) {
			return userLocale;
		}

		return getFallbackLocale();
	}

	public Locale getDefaultLocale() {
		return lookupLocale(uaaProperties.getAccount().getDefaultLocale());
	}

	public Locale getFallbackLocale() {
		return Locale.ENGLISH;
	}

	private Locale lookupLocale(final String localeName) {
		if (isBlank(localeName)) {
			return null;
		}

		for (final Locale l : Locale.getAvailableLocales()) {
			if (localeName.equalsIgnoreCase(localeToString(l))) {
				return l;
			}
		}

		return Locale.ENGLISH;
	}

	public String localeToString(final Locale locale) {
		return locale.getDisplayName(Locale.ENGLISH).toLowerCase();
	}
}
