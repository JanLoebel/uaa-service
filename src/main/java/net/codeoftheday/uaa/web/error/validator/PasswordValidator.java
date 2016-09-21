package net.codeoftheday.uaa.web.error.validator;

import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;
import static net.codeoftheday.uaa.web.error.ErrorResponseException.errorResponse;
import static net.codeoftheday.uaa.web.error.validator.PasswordValidator.PasswordError.PASSWORD_EMPTY;
import static net.codeoftheday.uaa.web.error.validator.PasswordValidator.PasswordError.PASSWORD_MAXLENGTH;
import static net.codeoftheday.uaa.web.error.validator.PasswordValidator.PasswordError.PASSWORD_MINLENGTH;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.web.error.ErrorMessage;

@Component
public class PasswordValidator {

	private final UaaProperties uaaProperties;
	private Pattern regexPattern = null;

	@Autowired
	public PasswordValidator(final UaaProperties uaaProperties) {
		this.uaaProperties = uaaProperties;
		final String regex = uaaProperties.getAccount().getPassword().getRegex();
		if (isNotBlank(regex)) {
			regexPattern = Pattern.compile(regex);
		}
	}

	public void validate(final String password) {
		// Validate password is given
		if (StringUtils.isBlank(password)) {
			throw errorResponse(errorBuilder().errorKey(PASSWORD_EMPTY));
		}

		// Validate min-length
		if (password.length() < uaaProperties.getAccount().getPassword().getMinLength()) {
			throw errorResponse(errorBuilder().errorKey(PASSWORD_MINLENGTH));
		}

		// Validate max-length
		if (password.length() > uaaProperties.getAccount().getPassword().getMaxLength()) {
			throw errorResponse(errorBuilder().errorKey(PASSWORD_MAXLENGTH));
		}

		// Validate regex if one is set
		if (regexPattern != null && !regexPattern.matcher(password).matches()) {
			throw errorResponse(errorBuilder().errorKey(PasswordError.PASSWORD_INVALID));
		}
	}

	public enum PasswordError {
		// @formatter:off
      @ErrorMessage("Given password is invalid.")
      PASSWORD_INVALID,

      @ErrorMessage("Given password is too long.")
      PASSWORD_MAXLENGTH,

      @ErrorMessage("Given password is too short.")
      PASSWORD_MINLENGTH,

      @ErrorMessage("Given password is empty.")
      PASSWORD_EMPTY,
      // @formatter:on
	}

}
