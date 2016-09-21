package net.codeoftheday.uaa.testutils;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.client.ClientRepository;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.domain.Client.ClientBuilder;

@Component
@Profile({ "test" })
public class TestClientComponent {

	public static final String CLIENT_ID = "client_id";
	public static final String CLIENT_SECRET = "client_secret";

	@Autowired
	private ClientRepository clientRepository;

	@PostConstruct
	public void createClients() {
		final ClientBuilder clientBuilder = Client.builder();
		clientBuilder.clientId(CLIENT_ID);
		clientBuilder.clientSecret(CLIENT_SECRET);
		clientBuilder.authorizedGrantTypes("implicit,refresh_token,password,authorization_code");
		clientBuilder.autoApprove("true");
		clientBuilder.scope("myScope");
		clientBuilder.authorities("READ, WRITE, ROLE_TRUSTED_CLIENT");
		clientBuilder.accessTokenValiditySeconds(3000);

		clientRepository.save(clientBuilder.build());
	}

}
