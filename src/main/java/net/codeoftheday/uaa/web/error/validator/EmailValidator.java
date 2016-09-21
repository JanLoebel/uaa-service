package net.codeoftheday.uaa.web.error.validator;

import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;
import static net.codeoftheday.uaa.web.error.ErrorResponseException.errorResponse;
import static net.codeoftheday.uaa.web.error.validator.EmailValidator.EmailError.EMAIL_EMPTY;
import static net.codeoftheday.uaa.web.error.validator.EmailValidator.EmailError.EMAIL_INVALID;
import static net.codeoftheday.uaa.web.error.validator.EmailValidator.EmailError.EMAIL_MAXLENGTH;
import static net.codeoftheday.uaa.web.error.validator.EmailValidator.EmailError.EMAIL_MINLENGTH;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.web.error.ErrorMessage;

@Component
public class EmailValidator {

	private final UaaProperties uaaProperties;

	@Autowired
	public EmailValidator(final UaaProperties uaaProperties) {
		this.uaaProperties = uaaProperties;
	}

	public void validate(final String email) {
		// Validate password is given
		if (StringUtils.isBlank(email)) {
			throw errorResponse(errorBuilder().errorKey(EMAIL_EMPTY));
		}

		// Validate min-length
		if (email.length() < uaaProperties.getAccount().getEmail().getMinLength()) {
			throw errorResponse(errorBuilder().errorKey(EMAIL_MINLENGTH));
		}

		// Validate max-length
		if (email.length() > uaaProperties.getAccount().getEmail().getMaxLength()) {
			throw errorResponse(errorBuilder().errorKey(EMAIL_MAXLENGTH));
		}

		// Validate against apache commons
		if (!isValidByApacheCommons(email)) {
			throw errorResponse(errorBuilder().errorKey(EMAIL_INVALID));
		}
	}

	private boolean isValidByApacheCommons(final String email) {
		//@formatter:off
		final org.apache.commons.validator.routines.EmailValidator validator
				= org.apache.commons.validator.routines.EmailValidator.getInstance(true);
		//@formatter:on
		return validator.isValid(email);
	}

	public enum EmailError {
		//@formatter:off
		@ErrorMessage("Given email is invalid.")
		EMAIL_INVALID,

		@ErrorMessage("Given email is too long.")
		EMAIL_MAXLENGTH,

		@ErrorMessage("Given email is too short.")
		EMAIL_MINLENGTH,

		@ErrorMessage("Given email is empty.")
		EMAIL_EMPTY
		//@formatter:on
	}
}
