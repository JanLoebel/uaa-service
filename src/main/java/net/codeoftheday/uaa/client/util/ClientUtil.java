package net.codeoftheday.uaa.client.util;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.join;
import static org.springframework.util.CollectionUtils.isEmpty;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.domain.Client.ClientBuilder;
import net.codeoftheday.uaa.domain.dto.ClientDto;

@Component
public class ClientUtil {

	@Autowired
	private UaaProperties uaaConfiguration;

	public Client.ClientBuilder basicClient() {
		final Client.ClientBuilder builder = Client.builder();

		builder.accessTokenValiditySeconds(uaaConfiguration.getClientDefault().getAccessTokenValiditySeconds());
		builder.authorizedGrantTypes(uaaConfiguration.getClientDefault().getAuthorizedGrantTypes());
		builder.autoApprove(uaaConfiguration.getClientDefault().getAutoApprove());
		builder.authorities(uaaConfiguration.getClientDefault().getAuthorizedGrantTypes());

		return builder;
	}

	public Client buildClient(final ClientDto clientDto) {
		final ClientBuilder builder = basicClient();

		// ClientId is a required field
		builder.clientId(clientDto.getClientId());

		// Client Secret
		if (isNotBlank(clientDto.getClientSecret())) {
			builder.clientSecret(clientDto.getClientSecret());
		}

		// Access Token Validity Seconds
		if (clientDto.getAccessTokenValiditySeconds() != null) {
			builder.accessTokenValiditySeconds(clientDto.getAccessTokenValiditySeconds());
		}

		// Authorized Grant Types
		if (!isEmpty(clientDto.getAuthorizedGrantTypes())) {
			builder.authorizedGrantTypes(join(clientDto.getAuthorizedGrantTypes(), ","));
		}

		// Auto Approve
		if (isNotBlank(clientDto.getAutoApprove())) {
			builder.autoApprove(clientDto.getAutoApprove());
		}

		// Scope
		if (!isEmpty(clientDto.getScope())) {
			builder.scope(join(clientDto.getScope(), ","));
		}

		return builder.build();
	}

	public Client updateClient(final Client clientToUpdate, final ClientDto clientDto) {

		// Client Secret
		if (isNotBlank(clientDto.getClientSecret())) {
			clientToUpdate.setClientSecret(clientDto.getClientSecret());
		}

		// Access Token Validity Seconds
		if (clientDto.getAccessTokenValiditySeconds() != null) {
			clientToUpdate.setAccessTokenValiditySeconds(clientDto.getAccessTokenValiditySeconds());
		}

		// Authorized Grant Types
		if (!isEmpty(clientDto.getAuthorizedGrantTypes())) {
			clientToUpdate.setAuthorizedGrantTypes(join(clientDto.getAuthorizedGrantTypes(), ","));
		}

		// Auto Approve
		if (isNotBlank(clientDto.getAutoApprove())) {
			clientToUpdate.setAutoApprove(clientDto.getAutoApprove());
		}

		// Scope
		if (!isEmpty(clientDto.getScope())) {
			clientToUpdate.setScope(join(clientDto.getScope(), ","));
		}

		return clientToUpdate;
	}

}
