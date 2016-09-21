package net.codeoftheday.uaa.domain.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.ToString;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.util.deserializer.StringSetDeserializer;

@ToString
public class ClientDto {

	@Getter
	@JsonProperty("client_id")
	private String clientId;

	@Getter
	@JsonProperty(value = "client_secret", access = Access.WRITE_ONLY)
	private String clientSecret;

	@Getter
	@JsonProperty("authorized_grant_types")
	@JsonDeserialize(using = StringSetDeserializer.class)
	private Set<String> authorizedGrantTypes;

	@Getter
	@JsonProperty("access_token_validity_seconds")
	private Integer accessTokenValiditySeconds;

	@Getter
	@JsonProperty("auto_approve")
	private String autoApprove;

	@Getter
	@JsonProperty("scope")
	@JsonDeserialize(using = StringSetDeserializer.class)
	private Set<String> scope;

	public ClientDto() {
		// JACKSON
	}

	public ClientDto(final Client client) {
		clientId = client.getClientId();
		// clientSecret will never be serialized
		authorizedGrantTypes = client.getAuthorizedGrantTypes();
		accessTokenValiditySeconds = client.getAccessTokenValiditySeconds();
		autoApprove = client.getAutoApprove();
		scope = client.getScope();
	}
}
