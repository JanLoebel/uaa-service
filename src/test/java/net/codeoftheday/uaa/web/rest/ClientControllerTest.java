package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_ID;
import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.client.ClientRepository;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.testutils.integration.AbstractMockMvcIntegrationTest;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;

@RunWith(SpringJUnit4ClassRunner.class)
@Transactional // After each test-method the data will be rolled back.
public class ClientControllerTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private ClientRepository clientRepository;

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void createClient() throws Exception {
		//@formatter:off
		final String clientId = "newlyCreatedClient";

		final JsonBuilder clientBuilder = jsonBuilder()
				.put("client_id", clientId)
				.put("scope", "my-web-app-scope")
				.put("auto_approve", "true");

		final RequestBuilder request = post("/client")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(clientBuilder.build()));

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isCreated())

			// Manually set
			.andExpect(jsonPath("$.client_id", is(clientId))).andExpect(jsonPath("$.auto_approve", is("true")))
			.andExpect(jsonPath("$.scope", hasSize(1))).andExpect(jsonPath("$.scope", hasItems("my-web-app-scope")))

			// client_secret will never be exposed
			.andExpect(jsonPath("$.client_secret").doesNotExist())

			// Default configuration properties so only check they exist
			.andExpect(jsonPath("$.access_token_validity_seconds").exists());
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void getClientIsPageable() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/client"))
			.andDo(print())
			.andExpect(status().isOk())

			// has pageable attributes
			.andExpect(jsonPath("$.totalPages").isNotEmpty()).andExpect(jsonPath("$.size").isNotEmpty())
			.andExpect(jsonPath("$.totalElements").isNotEmpty()).andExpect(jsonPath("$.first").isNotEmpty())
			.andExpect(jsonPath("$.last").isNotEmpty())

			// Check content exists
			.andExpect(jsonPath("$.content").isNotEmpty());
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteAuthority() throws Exception {
		//@formatter:off
		final Optional<Client> clientToDelete = clientRepository.findByClientId(CLIENT_ID);

		mockMvc()
			.perform(delete("/client/{id}", clientToDelete.get().getClientId()))
			.andDo(print())
			.andExpect(status().isOk());

		assertThat(clientRepository.findByClientId(CLIENT_ID).isPresent()).isFalse();
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateClient() throws Exception {
		//@formatter:off
		final String newlyScope = "newlyScope";

		final Client clientToUpdate = clientRepository.findByClientId(CLIENT_ID).get();

		final JsonBuilder clientBuilder = jsonBuilder()
				.put("scope", newlyScope)
				.put("auto_approve", "false");

		final RequestBuilder request = put("/client/{id}", clientToUpdate.getClientId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(clientBuilder.build()));

		mockMvc().perform(request).andDo(print())
				// ClientId won't be changed
				.andExpect(jsonPath("$.client_id", is(clientToUpdate.getClientId())))
				// Scope and auto_approve will be updated
				.andExpect(jsonPath("$.auto_approve", is("false"))).andExpect(jsonPath("$.scope", hasSize(1)))
				.andExpect(jsonPath("$.scope", hasItems(newlyScope)))

				// Not given fields will not be updated
				.andExpect(status().isOk());
		//@formatter:on
	}
}
