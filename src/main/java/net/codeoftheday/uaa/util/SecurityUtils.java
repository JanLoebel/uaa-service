package net.codeoftheday.uaa.util;

import java.util.Collection;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static String getCurrentUserUsername() {
		final Optional<UserDetails> userDetails = getUserDetails();
		if (userDetails.isPresent()) {
			return userDetails.get().getUsername();
		}

		return getStringPrincipal().orElse(null);
	}

	private static Optional<UserDetails> getUserDetails() {
		final Authentication authentication = getAuthentication();

		if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
			return Optional.of((UserDetails) authentication.getPrincipal());
		}

		return Optional.empty();
	}

	private static Optional<String> getStringPrincipal() {
		final Authentication authentication = getAuthentication();

		if (authentication.getPrincipal() instanceof String) {
			return Optional.of((String) authentication.getPrincipal());
		}

		return Optional.empty();
	}

	public static boolean hasAnyAuthority(final String... authorities) {
		final Authentication authentication = getAuthentication();
		if (authentication != null) {
			final Collection<? extends GrantedAuthority> userAuthorities = authentication.getAuthorities();
			for (final String authority : authorities) {
				if (userAuthorities.contains(new SimpleGrantedAuthority(authority))) {
					return true;
				}
			}
		}

		return false;
	}

	private static Authentication getAuthentication() {
		return SecurityContextHolder.getContext().getAuthentication();
	}

}
