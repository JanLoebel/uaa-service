package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;
import static org.springframework.http.HttpStatus.OK;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.domain.dto.UserDto;
import net.codeoftheday.uaa.email.AccountEmailService;
import net.codeoftheday.uaa.user.AccountService;
import net.codeoftheday.uaa.web.error.impl.AccountError;
import net.codeoftheday.uaa.web.error.validator.UserDtoRegisterValidator;

@RestController
@RequestMapping("/account")
public class AccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

	private final UaaProperties uaaProperties;
	private final UserDtoRegisterValidator userDtoRegisterValidator;
	private final AccountService accountService;
	private final AccountEmailService accountEmailService;

	@Autowired
	public AccountController(final UaaProperties uaaProperties, final UserDtoRegisterValidator userDtoRegisterValidator,
			final AccountService accountService, final AccountEmailService accountEmailService) {
		this.uaaProperties = uaaProperties;
		this.userDtoRegisterValidator = userDtoRegisterValidator;
		this.accountService = accountService;
		this.accountEmailService = accountEmailService;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody final UserDto userDto) {

		// Validate incoming object
		userDtoRegisterValidator.validate(userDto);

		// Validate user for duplications etc.
		accountService.validateUser(userDto);

		// Create user (ignore authorities -> each user will be a 'USER' automatically on creation)
		final User createdUser = accountService.registerAccount(userDto);

		// Send activation email if enabled on server
		if (uaaProperties.getAccount().isVerification()) {
			accountEmailService.sendActivationCodeMail(createdUser);
		}

		return new ResponseEntity<>(map(createdUser), HttpStatus.CREATED);
	}

	@PutMapping
	@PreAuthorize("hasAnyAuthority('USER')")
	public ResponseEntity<UserDto> updateAccount(@RequestBody final UserDto userDto) {
		// Validate new incoming password
		if (userDto.getPassword() != null) {
			userDtoRegisterValidator.getPasswordValidator().validate(userDto.getPassword());
		}

		final Optional<User> updatedUser = accountService.updateCurrentUserInformation(userDto);

		return updatedUser.map(u -> new ResponseEntity<>(map(u), OK)).orElseGet(() -> {
			LOGGER.warn("Current user is authenticated but can not be found!");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		});
	}

	@GetMapping
	@PreAuthorize("hasAnyAuthority('USER')")
	public ResponseEntity<?> getUser() {
		final Optional<User> currentUser = accountService.getCurrentUser();

		return currentUser.map(u -> new ResponseEntity<>(map(u), OK)).orElseGet(() -> {
			LOGGER.warn("Current user is authenticated but can not be found!");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		});
	}

	@GetMapping("/{id}/activate")
	public ResponseEntity<?> activateAccount(@PathVariable("id") final String userId,
			@RequestParam("code") final String activationCode) {

		// Check if account activation is needed
		if (!uaaProperties.getAccount().isVerification()) {
			return errorBuilder().errorKey(AccountError.ACCOUNT_ACTIVATION_DISABLED).build();
		}

		// Activate user
		final Optional<User> activatedUser = accountService.activateAccount(userId, activationCode);
		if (activatedUser.isPresent()) {
			return new ResponseEntity<>(map(activatedUser.get()), OK);
		}

		// Activation code is wrong
		// Use same message as user to avoid that a brute-force-attack get sensible information
		return errorBuilder().errorKey(AccountError.ACCOUNT_NOT_FOUND).build();
	}

	@PostMapping("/password/reset")
	public ResponseEntity<?> resetPassword(@RequestParam("email") final String email) {
		final Optional<User> user = accountService.resetPassword(email);

		if (user.isPresent()) {
			// Send email notification with the needed information
			accountEmailService.sendPasswordResetMail(user.get());
		}

		// Always return ok, so nobody knows if email address exists or not
		return new ResponseEntity<>(OK);
	}

	@PostMapping("/password/reset/confirm")
	public ResponseEntity<?> confirmPasswordReset(@RequestParam("email") final String email,
			@RequestParam("code") final String passwordResetCode, @RequestParam("password") final String newPassword) {

		// Validate incoming new password
		userDtoRegisterValidator.getPasswordValidator().validate(newPassword);

		final Optional<User> user = accountService.confirmResetPassword(email, passwordResetCode, newPassword);
		return user.map(u -> new ResponseEntity<>(map(u), OK)).orElseGet(() -> {
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		});
	}

	private static UserDto map(final User user) {
		return new UserDto(user);
	}

}
