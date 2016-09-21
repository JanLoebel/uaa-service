package net.codeoftheday.uaa.client;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.client.util.ClientUtil;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.domain.dto.ClientDto;

@Service
public class ClientService {
	private static final Logger LOGGER = LoggerFactory.getLogger(SpringClientDetailsService.class);

	private final ClientRepository clientRepository;
	private final ClientUtil clientUtil;

	@Autowired
	public ClientService(final ClientRepository clientRepository, final ClientUtil clientUtil) {
		this.clientRepository = clientRepository;
		this.clientUtil = clientUtil;
	}

	public Client createClient(final ClientDto clientDto) {
		final Optional<Client> loadedClient = clientRepository.findByClientId(clientDto.getClientId());

		loadedClient.ifPresent(c -> {
			throw new IllegalArgumentException("Given client with id: " + clientDto.getClientId() + " already exists!");
		});

		final Client toCreate = clientUtil.buildClient(clientDto);
		return clientRepository.save(toCreate);
	}

	public Client updateClient(final Client clientToUpdate, final ClientDto clientDto) {
		// Update existing client
		final Client client = clientUtil.updateClient(clientToUpdate, clientDto);
		return saveClient(client);
	}

	private Client saveClient(final Client client) {
		LOGGER.debug("Updating client: {}", client);
		return clientRepository.save(client);
	}

	@Transactional(readOnly = true)
	public Page<Client> findAll(final Pageable pageable) {
		return clientRepository.findAll(pageable);
	}

	@Transactional(readOnly = true)
	public Optional<Client> findById(final String id) {
		return clientRepository.findByClientId(id);
	}

	public void deleteById(final String clientId) {
		LOGGER.debug("Delete client: {}", clientId);
		final Optional<Client> client = clientRepository.findByClientId(clientId);
		client.ifPresent(c -> clientRepository.delete(c));
	}

}
