package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.authority.AuthorityRepository;
import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.testutils.integration.AbstractMockMvcIntegrationTest;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;

@RunWith(SpringRunner.class)
@Transactional // After each test-method the data will be rolled back.
public class AuthorityControllerTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Test
	@WithMockUser(authorities = "USER")
	public void userIsNotAllowedToCreateAuthority() throws Exception {
		//@formatter:off
		final JsonBuilder authorityToCreate = jsonBuilder()
				.put("name", "authorityName");

		final RequestBuilder request = post("/authority")
				.contentType(APPLICATION_JSON)
				.content(json(authorityToCreate.build()));

		mockMvc()
			.perform(request)
			.andExpect(status().isForbidden());
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void adminCanCreateAuthority() throws Exception {
		//@formatter:off
		final String newAuthorityName = "newAuthorityName";

		final JsonBuilder authorityToCreate = jsonBuilder()
				.put("name", newAuthorityName);

		final RequestBuilder request = post("/authority")
				.contentType(APPLICATION_JSON)
				.content(json(authorityToCreate.build()));

		mockMvc().perform(request).andDo(print())
				// 201 CREATED for new created authority
				.andExpect(status().isCreated())
				// id is returned
				.andExpect(jsonPath("$.id").isNotEmpty())
				.andExpect(jsonPath("$.name", is(newAuthorityName.toUpperCase())));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void getAuthoritiesIsPageable() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/authority"))
			.andDo(print())
			.andExpect(status().isOk())

			// has pageable attributes
			.andExpect(jsonPath("$.totalPages").isNotEmpty())
			.andExpect(jsonPath("$.size").isNotEmpty())
			.andExpect(jsonPath("$.totalElements").isNotEmpty())
			.andExpect(jsonPath("$.first").isNotEmpty())
			.andExpect(jsonPath("$.last").isNotEmpty())
			.andExpect(jsonPath("$.content").isNotEmpty());
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteAuthority() throws Exception {
		//@formatter:off
		final Optional<Authority> authorityToDelete = authorityRepository.findByName("USER");

		mockMvc()
			.perform(delete("/authority/{id}", authorityToDelete.get().getId()))
			.andDo(print())
			.andExpect(status().isOk());

		assertThat(authorityRepository.findByName("USER").isPresent()).isFalse();
		//@formatter:on
	}

}
