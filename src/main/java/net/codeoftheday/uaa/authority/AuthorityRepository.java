package net.codeoftheday.uaa.authority;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.codeoftheday.uaa.domain.Authority;

public interface AuthorityRepository extends JpaRepository<Authority, String> {

	Optional<Authority> findByName(String name);

	Optional<Authority> findById(String id);

}
