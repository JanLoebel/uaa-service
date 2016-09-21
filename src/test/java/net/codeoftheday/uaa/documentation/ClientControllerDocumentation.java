package net.codeoftheday.uaa.documentation;

import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_ID;
import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;

import net.codeoftheday.uaa.client.ClientRepository;
import net.codeoftheday.uaa.domain.Client;
import net.codeoftheday.uaa.testutils.integration.AbstractDocumentation;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;

@RunWith(SpringRunner.class)
public class ClientControllerDocumentation extends AbstractDocumentation {

	@Autowired
	private ClientRepository clientRepository;

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void clientGet() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/client").param("page", "0"))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				requestParameters(
					parameterWithName("page").optional().description("Page to retrieve")
				),
				responseFields(
					fieldWithPath("totalPages").type(NUMBER).description("Total pages of user"),
					fieldWithPath("totalElements").type(NUMBER).description("Total user"),
					fieldWithPath("size").type(NUMBER).description("Page size"),
					fieldWithPath("number").type(NUMBER).description("Current page number"),
					fieldWithPath("numberOfElements").type(NUMBER).description("Number of element of the current page"),
					fieldWithPath("first").type(BOOLEAN).description("Indicator if this page is the first"),
					fieldWithPath("last").type(BOOLEAN).description("Indicator if this page is the last"),
					fieldWithPath("content").type(ARRAY).description("Clients itself")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void clientCreate() throws Exception {
		//@formatter:off
		final JsonBuilder clientBuilder = jsonBuilder()
				.put("client_id", "my_client_id")
				.put("client_secret", "my_client_secret")
				.put("scope", "my-web-app-scope")
				.put("access_token_validity_seconds", 60)
				.put("authorized_grant_types", new String[] {"password", "refresh_scope"})
				.put("auto_approve", "true");

		final RequestBuilder createRequest = post("/client")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(clientBuilder.build()));

		mockMvc()
			.perform(createRequest)
			.andExpect(status().isCreated())
			.andDo(docHandler().document(
				requestFields(
					fieldWithPath("client_id").description("Client Identifier"),
					fieldWithPath("client_secret").optional().description("Client secret"),
					fieldWithPath("auto_approve").optional().description("Auto approve client"),
					fieldWithPath("scope").optional().description("Scope of the client"),
					fieldWithPath("access_token_validity_seconds").optional().type(NUMBER).description("Seconds for the token validity"),
					fieldWithPath("authorized_grant_types").optional().type(ARRAY).description("Grant types which are authorized for the client")
				),
				responseFields(
					fieldWithPath("client_id").description("Client Identifier"),
					fieldWithPath("auto_approve").description("Auto approve client"),
					fieldWithPath("scope").description("Scope of the client"),
					fieldWithPath("access_token_validity_seconds").type(NUMBER).description("Seconds for the token validity"),
					fieldWithPath("authorized_grant_types").type(ARRAY).description("Grant types which are authorized for the client")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void clientUpdate() throws Exception {
		//@formatter:off
		final JsonBuilder clientBuilder = jsonBuilder()
				.put("client_secret", "my_client_secret")
				.put("scope", "my-web-app-scope")
				.put("access_token_validity_seconds", 60)
				.put("authorized_grant_types", new String[] {"password", "refresh_scope"})
				.put("auto_approve", "true");

		final Client clientToUpdate = clientRepository.findByClientId(CLIENT_ID).get();
		final RequestBuilder request = put("/client/{id}", clientToUpdate.getClientId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(clientBuilder.build()));

		mockMvc()
			.perform(request)
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				requestFields(
					fieldWithPath("client_secret").optional().description("Client secret"),
					fieldWithPath("auto_approve").optional().description("Auto approve client"),
					fieldWithPath("scope").optional().description("Scope of the client"),
					fieldWithPath("access_token_validity_seconds").optional().type(NUMBER).description("Seconds for the token validity"),
					fieldWithPath("authorized_grant_types").optional().type(ARRAY).description("Grant types which are authorized for the client")
				),
				responseFields(
					fieldWithPath("client_id").description("Client Identifier"),
					fieldWithPath("auto_approve").description("Auto approve client"),
					fieldWithPath("scope").description("Scope of the client"),
					fieldWithPath("access_token_validity_seconds").type(NUMBER).description("Seconds for the token validity"),
					fieldWithPath("authorized_grant_types").type(ARRAY).description("Grant types which are authorized for the client")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void clientDelete() throws Exception {
		//@formatter:off
		final Optional<Client> clientToDelete = clientRepository.findByClientId(CLIENT_ID);

		mockMvc()
			.perform(delete("/client/{id}", clientToDelete.get().getClientId()))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				pathParameters(
					parameterWithName("id").description("Identifier of the user")
				)
			));
		//@formatter:on
	}

}
