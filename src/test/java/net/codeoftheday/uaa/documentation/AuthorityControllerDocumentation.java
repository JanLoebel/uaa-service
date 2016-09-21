package net.codeoftheday.uaa.documentation;

import static net.codeoftheday.uaa.testutils.TestUserComponent.ADMIN_USERNAME;
import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.springframework.http.MediaType.APPLICATION_JSON;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import net.codeoftheday.uaa.authority.AuthorityRepository;
import net.codeoftheday.uaa.domain.Authority;
import net.codeoftheday.uaa.testutils.integration.AbstractDocumentation;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;

@RunWith(SpringRunner.class)
public class AuthorityControllerDocumentation extends AbstractDocumentation {

	@Autowired
	private AuthorityRepository authorityRepository;

	@Test
	@WithMockUser(username = ADMIN_USERNAME, authorities = "ADMIN")
	public void authorityCreate() throws Exception {
		//@formatter:off
		final JsonBuilder authorityToCreate = jsonBuilder().put("name", "newAuthorityName");

		mockMvc()
			.perform(post("/authority")
				.contentType(APPLICATION_JSON)
				.content(json(authorityToCreate.build())))
			.andExpect(status().isCreated())
			.andDo(docHandler().document(
				requestFields(
					fieldWithPath("name").description("Name of the new authority")
				),
				responseFields(
					fieldWithPath("id").description("Id of the authority"),
					fieldWithPath("name").description("Name of the authority")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(username = ADMIN_USERNAME, authorities = "ADMIN")
	public void authorityDelete() throws Exception {
		//@formatter:off
		final Optional<Authority> authorityToDelete = authorityRepository.findByName("USER");

		mockMvc()
			.perform(delete("/authority/{id}", authorityToDelete.get().getId()))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				pathParameters(
					parameterWithName("id").description("Identifier of the authority")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void authorityGet() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/authority").param("page", "0"))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				requestParameters(
					parameterWithName("page").optional().description("Page to retrieve")
				),
				responseFields(
					fieldWithPath("totalPages").type(NUMBER).description("Total pages of authorities"),
					fieldWithPath("totalElements").type(NUMBER).description("Total authorities"),
					fieldWithPath("size").type(NUMBER).description("Page size"),
					fieldWithPath("number").type(NUMBER).description("Current page number"),
					fieldWithPath("numberOfElements").type(NUMBER).description("Number of element of the current page"),
					fieldWithPath("first").type(BOOLEAN).description("Indicator if this page is the first"),
					fieldWithPath("last").type(BOOLEAN).description("Indicator if this page is the last"),
					fieldWithPath("content").type(ARRAY).description("Authorities itself")
				)
			));
		//@formatter:on
	}

}
