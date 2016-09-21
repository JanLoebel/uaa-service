package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;
import static net.codeoftheday.uaa.web.error.impl.AccountError.ACCOUNT_DUPLICATE_USERNAME;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.domain.dto.UserDto;
import net.codeoftheday.uaa.user.UserService;
import net.codeoftheday.uaa.web.error.ErrorResponse;
import net.codeoftheday.uaa.web.error.impl.AccountError;

@RestController
@RequestMapping("/user")
public class UserController {

	private final UserService userService;

	@Autowired
	public UserController(final UserService userService) {
		this.userService = userService;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public Page<UserDto> findAll(@RequestParam(name = "page", defaultValue = "0") final int page) {
		return userService.findAll(new PageRequest(page, 50)).map(UserController::map);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public void deleteUser(@PathVariable("id") final String userId) {
		userService.deleteUserById(userId);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateUser(@PathVariable("id") final String userId, @RequestBody final UserDto userDto) {
		// @Valid is ignored here because the user only get updated.

		final Optional<User> userToUpdate = userService.findById(userId);
		if (!userToUpdate.isPresent()) {
			return errorBuilder().errorKey(AccountError.ACCOUNT_NOT_FOUND).build();
		}

		final User updatedUser = userService.updateUser(userToUpdate.get(), userDto);
		return new ResponseEntity<>(map(updatedUser), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createUser(@Valid @RequestBody final UserDto userDto) {
		// Validate user
		final Optional<ErrorResponse> error = validateUser(userDto);
		if (error.isPresent()) {
			return error.get().build();
		}

		final User createdUser = userService.createUser(userDto);
		final User updatedUser = userService.updateUser(createdUser, userDto);

		return new ResponseEntity<>(map(updatedUser), HttpStatus.CREATED);
	}

	private static UserDto map(final User user) {
		return new UserDto(user);
	}

	private Optional<ErrorResponse> validateUser(final UserDto userDto) {
		// Username is already taken
		final Optional<User> userWithSameUserName = userService.findUserByUsername(userDto.getUsername());
		if (userWithSameUserName.isPresent()) {
			return Optional.of(errorBuilder().errorKey(ACCOUNT_DUPLICATE_USERNAME));
		}

		// Email is already taken
		final Optional<User> userWithSameEmail = userService.findUserByEmail(userDto.getEmail());
		if (userWithSameEmail.isPresent()) {
			return Optional.of(errorBuilder().errorKey(AccountError.ACCOUNT_DUPLICATE_EMAIL));
		}

		return Optional.empty();
	}

}
