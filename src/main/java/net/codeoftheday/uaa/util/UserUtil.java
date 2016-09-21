package net.codeoftheday.uaa.util;

import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.authority.AuthorityService;
import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.domain.dto.UserDto;
import net.codeoftheday.uaa.user.UserLocaleResolver;

@Component
public class UserUtil {

	private final PasswordEncoder passwordEncoder;
	private final AuthorityService authorityService;
	private final UserLocaleResolver userLocaleResolver;

	@Autowired
	public UserUtil(final PasswordEncoder passwordEncoder, final AuthorityService authorityService,
			final UserLocaleResolver userLocaleResolver) {
		this.passwordEncoder = passwordEncoder;
		this.authorityService = authorityService;
		this.userLocaleResolver = userLocaleResolver;
	}

	public User updateUser(final User userToUpdate, final UserDto userDto) {
		// Email
		if (isNotBlank(userDto.getEmail())) {
			userToUpdate.setEmail(userDto.getEmail().trim().toLowerCase());
		}

		// Password
		if (isNotBlank(userDto.getPassword())) {
			userToUpdate.setPassword(passwordEncoder.encode(userDto.getPassword()));
		}

		// Locale
		if (isNotBlank(userDto.getLocale())) {
			userToUpdate.setLocale(userLocaleResolver.resolveLocale(userDto.getLocale()));
		}

		// Authorities
		if (!isEmpty(userDto.getAuthorities())) {
			final Set<Authority> authorities = userDto.getAuthorities().stream()
					.map(authorityString -> authorityService.findByName(authorityString.trim()))
					.filter(a -> a.isPresent()).map(a -> a.get()).collect(toSet());

			userToUpdate.setAuthorities(authorities);
		}

		// ActivationToken
		if (!isNotBlank(userDto.getActivationCode())) {
			userToUpdate.setActivationCode(userDto.getActivationCode());
		}

		return userToUpdate;
	}
}
