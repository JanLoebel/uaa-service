package net.codeoftheday.uaa.authority;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.user.UserRepository;

@Service
public class AuthorityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthorityService.class);

	private final AuthorityRepository authorityRepository;
	private final UserRepository userRepository;

	@Autowired
	public AuthorityService(final AuthorityRepository authorityRepository, final UserRepository userRepository) {
		this.authorityRepository = authorityRepository;
		this.userRepository = userRepository;
	}

	@Transactional
	public Authority createNewAuthority(final String name) {
		final Authority createdAuthority = new Authority(name.toUpperCase());

		LOGGER.debug("Created and saving Authority with name: {} -> {}", name, createdAuthority);
		return authorityRepository.save(createdAuthority);
	}

	@Transactional(readOnly = true)
	public Page<Authority> findAllAuthorities(final Pageable page) {
		return authorityRepository.findAll(page);
	}

	@Transactional(readOnly = true)
	public Optional<Authority> findByName(final String authorityName) {
		return authorityRepository.findByName(authorityName.toUpperCase());
	}

	@Transactional
	public void deleteById(final String authorityId) {
		LOGGER.debug("Delete authority: {}", authorityId);

		final Optional<Authority> authorityToDelete = authorityRepository.findById(authorityId);
		authorityToDelete.ifPresent(a -> {

			// Remove authority from all users (unidirectional relationship)
			final List<User> userContainsAuthority = userRepository.findByAuthoritiesContains(a);
			for (final User user : userContainsAuthority) {
				user.getAuthorities().remove(a);
			}
			userRepository.save(userContainsAuthority);

			// Delete authority itself
			authorityRepository.delete(a);
		});
	}

}
