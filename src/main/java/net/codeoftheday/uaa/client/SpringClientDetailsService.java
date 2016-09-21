
package net.codeoftheday.uaa.client;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

import net.codeoftheday.uaa.domain.Client;

@Service
public class SpringClientDetailsService implements ClientDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(SpringClientDetailsService.class);

	@Autowired
	private ClientRepository clientRepository;

	@Override
	public ClientDetails loadClientByClientId(final String clientId) {
		final Optional<Client> client = clientRepository.findByClientId(clientId);

		if (client.isPresent()) {
			return client.get();
		}

		LOGGER.warn("Client with clientId: '{}' could not be found!", clientId);
		throw new ClientRegistrationException("Invalid client");
	}

}
