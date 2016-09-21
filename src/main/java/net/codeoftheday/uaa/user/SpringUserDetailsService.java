package net.codeoftheday.uaa.user;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import net.codeoftheday.uaa.domain.User;

@Service
public class SpringUserDetailsService implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringUserDetailsService.class);

	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(final String username) {
		LOGGER.debug("Authenticating {}", username);

		final String lowercaseUsername = username.toLowerCase();
		final Optional<User> loadedUser = userRepository.findUserByUsernameIgnoreCase(lowercaseUsername);

		return loadedUser.map(user -> {
			if (isNotBlank(user.getActivationCode())) {
				// ActivationCode of user is set so the account is not yet activated.
				throw new DisabledException("Given user is not yet activated.");
			}

			if (isNotBlank(user.getPasswordResetCode())) {
				// PasswordResetCode of user is set so the account is disabled till the password was reseted.
				throw new DisabledException("Given user has requested password reset.");
			}

			// Map authorities
			final List<SimpleGrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
					.map(authority -> new SimpleGrantedAuthority(authority.getName())).collect(Collectors.toList());

			// Return converted user
			return springSecurityUser(lowercaseUsername, user.getPassword(), grantedAuthorities);
		}).orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseUsername + " was not found"));
	}

	private static org.springframework.security.core.userdetails.User springSecurityUser(final String username,
			final String password, final List<SimpleGrantedAuthority> authorities) {
		return new org.springframework.security.core.userdetails.User(username, password, authorities);
	}

}
