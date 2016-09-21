package net.codeoftheday.uaa.domain.dto;

import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.ToString;
import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.util.jsonfilter.impl.roles.JsonVisibleRoles;

@ToString(exclude = "password")
@JsonFilter("roles")
public class UserDto {

	@Getter
	@JsonProperty(access = Access.READ_ONLY)
	private String id;

	@Getter
	private String username;

	@Getter
	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	@Getter
	private String email;

	@Getter
	private Set<String> authorities;

	@Getter
	private String locale;

	@Getter
	@JsonVisibleRoles(roles = "ADMIN")
	private String activationCode;

	public UserDto() {
		// JACKSON
	}

	public UserDto(final User user) {
		this(user.getId(), user.getUsername(), user.getPassword(), user.getEmail(),
				user.getAuthorities().stream().map(Authority::getName).collect(Collectors.toSet()), user.getLocale(),
				user.getActivationCode());
	}

	protected UserDto(final String id, final String username, final String password, final String email,
			final Set<String> authorities, final String locale, final String activationCode) {
		this.id = id;
		this.username = username;
		this.password = password;
		this.email = email;
		this.authorities = authorities;
		this.locale = locale;
		this.activationCode = activationCode;
	}
}
