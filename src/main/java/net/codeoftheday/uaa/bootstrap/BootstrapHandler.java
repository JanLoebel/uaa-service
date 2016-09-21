package net.codeoftheday.uaa.bootstrap;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.authority.AuthorityService;
import net.codeoftheday.uaa.client.ClientService;
import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.domain.dto.ClientDto;
import net.codeoftheday.uaa.domain.dto.UserDto;
import net.codeoftheday.uaa.user.UserService;

@Component
public class BootstrapHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(BootstrapHandler.class);

	@Autowired
	private UserService userService;

	@Autowired
	private ClientService clientService;

	@Autowired
	private AuthorityService authorityService;

	@Autowired
	private BootstrapReader bootstrapReader;

	@Autowired
	private UaaProperties uaaProperties;

	public void setup() throws IOException {
		final Optional<Bootstrap> bootstrap = bootstrapReader.readBootstrap(uaaProperties.getBootstrapFile());
		if (!bootstrap.isPresent()) {
			LOGGER.info("No bootstrap configuration found, so skip initalization.");
			return;
		}

		LOGGER.info("Boostrap starting...");

		// Setup clients
		setupClients(bootstrap.get().getClients());

		// Setup users
		setupUsers(bootstrap.get().getUsers());

		LOGGER.info("Bootstrap done.");
	}

	private void setupUsers(final List<UserDto> users) {
		LOGGER.info("Creating {} user.", users.size());
		for (final UserDto userDto : users) {

			// Setup authorities
			setupAuthorities(userDto.getAuthorities());

			// Create the user
			final Optional<User> loadedUser = userService.findUserByUsername(userDto.getUsername());
			if (loadedUser.isPresent()) {
				LOGGER.info("User with username: '{}' already exists and will be skipped.", userDto.getUsername());
			} else {
				LOGGER.debug("Creating user: {}", userDto);
				userService.createUser(userDto);
			}
		}
	}

	private void setupAuthorities(final Set<String> authorities) {
		if (authorities == null) {
			return;
		}

		for (final String authority : authorities) {
			final Optional<Authority> foundAuthority = authorityService.findByName(authority);
			if (foundAuthority.isPresent()) {
				LOGGER.info("Authority with name: '{}' already exists and will be skipped.", authority);
			} else {
				LOGGER.info("Creating authority: {}", authority);
				authorityService.createNewAuthority(authority);
			}
		}
	}

	private void setupClients(final List<ClientDto> clients) {
		for (final ClientDto clientDto : clients) {
			final Optional<Client> loadedClient = clientService.findById(clientDto.getClientId());
			if (loadedClient.isPresent()) {
				LOGGER.info("Client with clientId: '{}' already exists and will be skipped.", clientDto.getClientId());
			} else {
				LOGGER.info("Creating client: {}", clientDto.getClientId());
				clientService.createClient(clientDto);
			}
		}
	}
}
