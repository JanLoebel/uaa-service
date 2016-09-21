package net.codeoftheday.uaa.testutils;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.authority.AuthorityRepository;
import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.user.UserRepository;

@Component
@Profile({ "test" })
public class TestUserComponent {

	public static final String USER_USERNAME = "user";
	public static final String USER_PASSWORD = "password";
	public static final String USER_EMAIL = "user@email.com";
	public static final String USER_LOCALE = "english";

	public static final String ADMIN_USERNAME = "admin";
	public static final String ADMIN_PASSWORD = "password";
	public static final String ADMIN_EMAIL = "admin@email.com";
	public static final String ADMIN_LOCALE = "english";

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private AuthorityRepository authRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@PostConstruct
	public void createTestUser() {
		createUser();
		createAdminUser();
	}

	private void createAdminUser() {
		if (userRepository.findUserByUsernameIgnoreCase(ADMIN_USERNAME).isPresent()) {
			return;
		}

		// Admin User
		final User admin = new User();
		admin.setUsername(ADMIN_USERNAME);
		admin.setPassword(encode(ADMIN_PASSWORD));
		admin.setEmail(ADMIN_EMAIL);
		admin.setLocale(ADMIN_LOCALE);
		admin.setAuthorities(authorities("USER", "ADMIN"));
		userRepository.save(admin);
	}

	private void createUser() {
		if (userRepository.findUserByUsernameIgnoreCase(USER_USERNAME).isPresent()) {
			return;
		}

		// Normal User
		final User user = new User();
		user.setUsername(USER_USERNAME);
		user.setPassword(encode(USER_PASSWORD));
		user.setEmail(USER_EMAIL);
		user.setLocale(USER_LOCALE);
		user.setAuthorities(authorities("USER"));
		userRepository.save(user);
	}

	public String encode(final String rawPassword) {
		return passwordEncoder.encode(rawPassword);
	}

	private Set<Authority> authorities(final String... authorities) {
		final Set<Authority> authoritySet = new HashSet<>();

		for (final String authority : authorities) {
			final Optional<Authority> loadedAuthority = authRepository.findByName(authority.toUpperCase());

			loadedAuthority.map(a -> authoritySet.add(a)).orElseGet(() -> {
				authoritySet.add(authRepository.save(new Authority(authority)));
				return null;
			});
		}

		return authoritySet;
	}

}
