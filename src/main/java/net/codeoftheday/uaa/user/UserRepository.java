package net.codeoftheday.uaa.user;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.domain.User;

public interface UserRepository extends JpaRepository<User, String> {

	Optional<User> findById(String id);

	Optional<User> findUserByUsernameIgnoreCase(String username);

	Optional<User> findUserByEmailIgnoreCase(String email);

	List<User> findByAuthoritiesContains(Authority authority);

}
