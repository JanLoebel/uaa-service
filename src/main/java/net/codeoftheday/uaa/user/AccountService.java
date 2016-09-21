package net.codeoftheday.uaa.user;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.time.temporal.ChronoUnit.MINUTES;
import static net.codeoftheday.uaa.util.CodeGeneratorUtils.generateActivationCode;
import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;
import static net.codeoftheday.uaa.web.error.ErrorResponseException.errorResponse;
import static org.apache.commons.lang3.StringUtils.isBlank;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.authority.AuthorityService;
import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.domain.dto.UserDto;
import net.codeoftheday.uaa.util.CodeGeneratorUtils;
import net.codeoftheday.uaa.util.SecurityUtils;
import net.codeoftheday.uaa.web.error.impl.AccountError;

@Service
public class AccountService {

	private static final String DEFAULT_AUTHORITY = "USER";

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

	private final UaaProperties uaaProperties;
	private final UserRepository userRepository;
	private final AuthorityService authorityService;
	private final PasswordEncoder passwordEncoder;
	private final UserLocaleResolver userLocaleResolver;

	@Autowired
	public AccountService(final UaaProperties uaaProperties, final UserRepository userRepository,
			final AuthorityService authorityService, final PasswordEncoder passwordEncoder,
			final UserLocaleResolver userLocaleResolver) {
		this.uaaProperties = uaaProperties;
		this.userRepository = userRepository;
		this.authorityService = authorityService;
		this.passwordEncoder = passwordEncoder;
		this.userLocaleResolver = userLocaleResolver;
	}

	@Transactional
	public User registerAccount(final UserDto userDto) {
		final User user = new User();

		// Username and email will be saved lower case
		user.setUsername(userDto.getUsername().toLowerCase());
		user.setEmail(userDto.getEmail().toLowerCase());
		user.setPassword(passwordEncoder.encode(checkNotNull(userDto.getPassword())));
		user.setLocale(userLocaleResolver.resolveLocale(userDto.getLocale()));

		// Only generate verification code if server has enabled verification
		if (uaaProperties.getAccount().isVerification()) {
			user.setActivationCode(generateActivationCode());
		}

		// Each user is by default a normal user.
		user.addAuthority(authorityService.findByName(DEFAULT_AUTHORITY).get());

		LOGGER.debug("Created and saving User with Username: {} - {}.", userDto.getUsername(), user);
		return userRepository.save(user);
	}

	@Transactional
	public Optional<User> updateCurrentUserInformation(final UserDto userDto) {
		return getCurrentUser().map(u -> {
			// Email, username and authorities can't be changed

			// Only update password if a new one is set
			if (userDto.getPassword() != null) {
				// Validate password
				u.setPassword(passwordEncoder.encode(userDto.getPassword()));
			}

			// Only update if it was set
			if (userDto.getLocale() != null) {
				u.setLocale(userLocaleResolver.resolveLocale(userDto.getLocale()));
			}

			LOGGER.debug("Changed Information for User: {}", u);
			return userRepository.save(u);
		});
	}

	@Transactional(readOnly = true)
	public Optional<User> getCurrentUser() {
		return userRepository.findUserByUsernameIgnoreCase(SecurityUtils.getCurrentUserUsername());
	}

	public void validateUser(final UserDto userDto) {
		// Username is already taken
		final Optional<User> userWithSameUserName = userRepository.findUserByUsernameIgnoreCase(userDto.getUsername());
		if (userWithSameUserName.isPresent()) {
			throw errorResponse(errorBuilder().errorKey(AccountError.ACCOUNT_DUPLICATE_USERNAME)
					.message("Given username is already in use."));
		}

		// Email is already taken
		final Optional<User> userWithSameEmail = userRepository.findUserByEmailIgnoreCase(userDto.getEmail());
		if (userWithSameEmail.isPresent()) {
			throw errorResponse(errorBuilder().errorKey(AccountError.ACCOUNT_DUPLICATE_EMAIL)
					.message("Given email is already in use."));
		}

	}

	@Transactional
	public Optional<User> activateAccount(final String userId, final String activationCode) {
		final Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			final User account = user.get();

			// Is activation code correct
			if (account.getActivationCode().equals(activationCode)) {
				account.setActivationCode(null);
				return Optional.of(userRepository.save(account));
			}
		}

		return Optional.empty();
	}

	@Transactional
	public Optional<User> resetPassword(final String email) {
		final Optional<User> user = userRepository.findUserByEmailIgnoreCase(email);
		if (user.isPresent()) {
			final User account = user.get();

			// Only reset password again if it is still valid
			if (!passwordResetCodeStillValid(account)) {
				LOGGER.debug("Reset password process for email: {}.", account.getEmail());

				account.setPasswordResetTimestamp(Instant.now());
				account.setPasswordResetCode(CodeGeneratorUtils.generatePasswordResetCode());
				account.setPassword(passwordEncoder.encode(CodeGeneratorUtils.generatePasswordResetCode()));
				return Optional.of(userRepository.save(account));
			}
		}

		return Optional.empty();
	}

	private boolean passwordResetCodeStillValid(final User user) {
		final Instant passwordResetTimestamp = user.getPasswordResetTimestamp();
		if (passwordResetTimestamp == null) {
			return false;
		}

		final int passwordResetValidityMinutes = uaaProperties.getAccount().getPasswordResetCodeValidityMinutes();
		final Instant minutesBefore = Instant.now().minus(passwordResetValidityMinutes, MINUTES);
		return passwordResetTimestamp.isBefore(minutesBefore);
	}

	@Transactional
	public Optional<User> confirmResetPassword(final String email, final String passwordResetCode,
			final String newPassword) {

		if (!isBlank(email) && !isBlank(passwordResetCode)) {
			final Optional<User> user = userRepository.findUserByEmailIgnoreCase(email);
			if (user.isPresent() && !isBlank(user.get().getPasswordResetCode())) {
				final User account = user.get();

				// Validate timestamp
				if (passwordResetCodeStillValid(account)) {
					throw errorResponse(errorBuilder().errorKey(AccountError.ACCOUNT_PASSWORD_RESET_OUTDATED));
				}

				// Validate password-code
				if (passwordResetCode.equals(account.getPasswordResetCode())) {
					// Reset password
					account.setPassword(passwordEncoder.encode(newPassword));
					account.setPasswordResetTimestamp(null);
					account.setPasswordResetCode(null);
					return Optional.of(userRepository.save(account));
				}
			}
		}

		throw errorResponse(errorBuilder().errorKey(AccountError.ACCOUNT_PASSWORD_RESET_INVALID));
	}

}
