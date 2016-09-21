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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.codeoftheday.uaa.authority.AuthorityService;
import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.domain.dto.AuthorityDto;
import net.codeoftheday.uaa.web.error.impl.AuthorityError;

@RestController
@RequestMapping("/authority")
public class AuthorityController {

	private final AuthorityService authorityService;

	@Autowired
	public AuthorityController(final AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@GetMapping
	public Page<Authority> findAll(@RequestParam(name = "page", defaultValue = "0") final int page) {
		return authorityService.findAllAuthorities(new PageRequest(page, 50));
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@PostMapping
	public ResponseEntity<?> create(@RequestBody @Valid final AuthorityDto authority) {
		final Optional<Authority> existingAuthority = authorityService.findByName(authority.getName());

		if (existingAuthority.isPresent()) {
			return errorBuilder().errorKey(AuthorityError.AUTHORITY_DUPLICATE).build();
		}

		final Authority createdAuthority = authorityService.createNewAuthority(authority.getName());
		return new ResponseEntity<>(new AuthorityDto(createdAuthority), HttpStatus.CREATED);
	}

	@PreAuthorize("hasAuthority('ADMIN')")
	@DeleteMapping("/{id}")
	public void delete(@PathVariable("id") final String authorityId) {
		authorityService.deleteById(authorityId);
	}

}
