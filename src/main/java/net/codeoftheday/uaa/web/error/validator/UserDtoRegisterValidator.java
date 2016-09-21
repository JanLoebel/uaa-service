package net.codeoftheday.uaa.web.error.validator;

import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;
import static net.codeoftheday.uaa.web.error.ErrorResponseException.errorResponse;
import static net.codeoftheday.uaa.web.error.validator.UserDtoRegisterValidator.UsernameError.USERNAME_EMPTY;
import static net.codeoftheday.uaa.web.error.validator.UserDtoRegisterValidator.UsernameError.USERNAME_INVALID;
import static net.codeoftheday.uaa.web.error.validator.UserDtoRegisterValidator.UsernameError.USERNAME_MAXLENGTH;
import static net.codeoftheday.uaa.web.error.validator.UserDtoRegisterValidator.UsernameError.USERNAME_MINLENGTH;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.domain.dto.UserDto;
import net.codeoftheday.uaa.web.error.ErrorMessage;

@Component
public class UserDtoRegisterValidator {

	private final UaaProperties uaaProperties;
	private final PasswordValidator passwordValidator;
	private final EmailValidator emailValidator;

	private Pattern regexPattern = null;

	@Autowired
	public UserDtoRegisterValidator(final UaaProperties uaaProperties, final PasswordValidator passwordValidator,
			final EmailValidator emailValidator) {
		this.uaaProperties = uaaProperties;
		this.passwordValidator = passwordValidator;
		this.emailValidator = emailValidator;

		final String regex = uaaProperties.getAccount().getUsername().getRegex();
		if (isNotBlank(regex)) {
			regexPattern = Pattern.compile(regex);
		}
	}

	public void validate(final UserDto userDto) {
		validateUsername(userDto.getUsername());
		passwordValidator.validate(userDto.getPassword());
		emailValidator.validate(userDto.getEmail());
	}

	private void validateUsername(final String username) {
		if (StringUtils.isBlank(username)) {
			throw errorResponse(errorBuilder().errorKey(USERNAME_EMPTY));
		}

		// Validate min-length
		if (username.length() < uaaProperties.getAccount().getUsername().getMinLength()) {
			throw errorResponse(errorBuilder().errorKey(USERNAME_MINLENGTH));
		}

		// Validate max-length
		if (username.length() > uaaProperties.getAccount().getUsername().getMaxLength()) {
			throw errorResponse(errorBuilder().errorKey(USERNAME_MAXLENGTH));
		}

		// Validate regex if one is set
		if (regexPattern != null && !regexPattern.matcher(username).matches()) {
			throw errorResponse(errorBuilder().errorKey(USERNAME_INVALID));
		}
	}

	public PasswordValidator getPasswordValidator() {
		return this.passwordValidator;
	}

	public EmailValidator getEmaikValidator() {
		return this.emailValidator;
	}

	public enum UsernameError {
		// @formatter:off
      @ErrorMessage("Given username is invalid.")
      USERNAME_INVALID,

      @ErrorMessage("Given username is too long.")
      USERNAME_MAXLENGTH,

      @ErrorMessage("Given username is too short.")
      USERNAME_MINLENGTH,

      @ErrorMessage("Given username is empty.")
      USERNAME_EMPTY,
      // @formatter:on
	}

}
