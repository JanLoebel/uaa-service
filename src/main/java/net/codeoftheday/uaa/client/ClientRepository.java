package net.codeoftheday.uaa.client;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.codeoftheday.uaa.domain.Client;

public interface ClientRepository extends JpaRepository<Client, String> {

	Optional<Client> findByClientId(String clientId);

}
