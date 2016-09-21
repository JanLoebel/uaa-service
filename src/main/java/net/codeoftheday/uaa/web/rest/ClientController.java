package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;

import java.util.Optional;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.codeoftheday.uaa.client.ClientService;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.domain.dto.ClientDto;
import net.codeoftheday.uaa.web.error.impl.ClientError;
import net.codeoftheday.uaa.web.error.validator.ClientValidator;

@RestController
@RequestMapping("/client")
public class ClientController {

	private final ClientService clientService;
	private final ClientValidator clientValidator;

	@Autowired
	public ClientController(final ClientService clientService, final ClientValidator clientValidator) {
		this.clientService = clientService;
		this.clientValidator = clientValidator;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public Page<ClientDto> findAll(@RequestParam(name = "page", defaultValue = "0") final int page) {
		return clientService.findAll(new PageRequest(page, 50)).map(client -> new ClientDto(client));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") final String authorityId) {
		clientService.deleteById(authorityId);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PutMapping("/{id}")
	public ResponseEntity<?> updateClient(@PathVariable("id") final String clientId,
			@RequestBody @Valid final ClientDto clientDto) {
		final Optional<Client> clientToUpdate = clientService.findById(clientId);
		if (!clientToUpdate.isPresent()) {
			return errorBuilder().errorKey(ClientError.CLIENT_NOT_FOUND).build();
		}

		// Save client
		final Client updatedClient = clientService.updateClient(clientToUpdate.get(), clientDto);

		return new ResponseEntity<>(map(updatedClient), HttpStatus.OK);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public ResponseEntity<?> createClient(@RequestBody @Valid final ClientDto clientDto) {
		clientValidator.validate(clientDto);

		final Optional<Client> loadedClient = clientService.findById(clientDto.getClientId());
		if (loadedClient.isPresent()) {
			return errorBuilder().errorKey(ClientError.CLIENT_DUPLICATE).build();
		}

		return new ResponseEntity<>(map(clientService.createClient(clientDto)), HttpStatus.CREATED);
	}

	private static ClientDto map(final Client client) {
		return new ClientDto(client);
	}

}
