package net.codeoftheday.uaa.user;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.domain.dto.UserDto;
import net.codeoftheday.uaa.util.UserUtil;

@Service
public class UserService {
	private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;
	private final AccountService accountService;
	private final UserUtil userUtil;

	@Autowired
	public UserService(final UserRepository userRepository, final AccountService accountService,
			final UserUtil userUtil) {
		this.userRepository = userRepository;
		this.accountService = accountService;
		this.userUtil = userUtil;
	}

	@Transactional(readOnly = true)
	public Optional<User> findById(final String id) {
		return userRepository.findById(id);
	}

	@Transactional(readOnly = true)
	public Optional<User> findUserByUsername(final String username) {
		return userRepository.findUserByUsernameIgnoreCase(username);
	}

	@Transactional(readOnly = true)
	public Optional<User> findUserByEmail(final String email) {
		return userRepository.findUserByEmailIgnoreCase(email);
	}

	@Transactional(readOnly = true)
	public Page<User> findAll(final Pageable page) {
		return userRepository.findAll(page);
	}

	public void deleteUserById(final String userId) {
		LOGGER.debug("Delete user: {}", userId);

		final Optional<User> userToDelete = userRepository.findById(userId);
		userToDelete.ifPresent(u -> userRepository.delete(u));
	}

	private User saveUser(final User user) {
		LOGGER.debug("Saving user: {}", user);
		return userRepository.save(user);
	}

	public User updateUser(final User userToUpdate, final UserDto userDto) {
		// Update existing user
		final User user = userUtil.updateUser(userToUpdate, userDto);

		// Save updated User
		return saveUser(user);
	}

	public User createUser(final UserDto userDto) {
		// Create basic user
		final User createdUser = accountService.registerAccount(userDto);

		// Update user with all given information
		final User updatedUser = userUtil.updateUser(createdUser, userDto);

		// Reset activation code to avoid a user without set activation code has to activate his account
		updatedUser.setActivationCode(userDto.getActivationCode());

		// Save created user
		return saveUser(updatedUser);
	}

}
