package net.codeoftheday.uaa.domain;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.util.StringUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@ToString
@AllArgsConstructor
@Builder
public class Client implements ClientDetails {

	private static final long serialVersionUID = -1L;

	@Id
	@NotNull
	@Size(min = 1, max = 50)
	@Column(length = 50)
	@Getter
	@Setter
	private String clientId;

	@Getter
	@Setter
	private Integer accessTokenValiditySeconds;

	@Setter
	private String authorities;

	@Setter
	private String authorizedGrantTypes;

	@Getter
	@Setter
	private String autoApprove;

	@Getter
	@Setter
	private String clientSecret;

	@Getter
	@Setter
	private Integer refreshTokenValiditySeconds;

	@Setter
	private String resourceIds;

	@Setter
	private String scope;

	@Setter
	private String registeredRedirectUri;

	public Client() {
		// JPA
	}

	@Override
	public Map<String, Object> getAdditionalInformation() {
		// Not implemented
		return null;
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return AuthorityUtils.commaSeparatedStringToAuthorityList(authorities);
	}

	@Override
	public Set<String> getAuthorizedGrantTypes() {
		return StringUtils.commaDelimitedListToSet(authorizedGrantTypes);
	}

	@Override
	public Set<String> getRegisteredRedirectUri() {
		return StringUtils.commaDelimitedListToSet(registeredRedirectUri);
	}

	@Override
	public Set<String> getResourceIds() {
		return StringUtils.commaDelimitedListToSet(resourceIds);
	}

	@Override
	public Set<String> getScope() {
		if (!isScoped()) {
			// Specification say, empty if the client is not scoped
			return Collections.emptySet();
		}

		return StringUtils.commaDelimitedListToSet(scope);
	}

	@Override
	public boolean isAutoApprove(final String scope) {
		if (autoApprove == null) {
			return false;
		}

		for (final String auto : StringUtils.commaDelimitedListToSet(autoApprove)) {
			if ("true".equalsIgnoreCase(auto) || scope.matches(auto)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean isScoped() {
		return !StringUtils.isEmpty(scope);
	}

	@Override
	public boolean isSecretRequired() {
		return !StringUtils.isEmpty(clientSecret);
	}

}
