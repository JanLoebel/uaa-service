package net.codeoftheday.uaa.documentation;

import static net.codeoftheday.uaa.testutils.TestUserComponent.USER_USERNAME;
import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.JsonFieldType.BOOLEAN;
import static org.springframework.restdocs.payload.JsonFieldType.NUMBER;
import static org.springframework.restdocs.payload.JsonFieldType.STRING;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.testutils.integration.AbstractDocumentation;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;
import net.codeoftheday.uaa.user.UserRepository;

@RunWith(SpringRunner.class)
public class UserControllerDocumentation extends AbstractDocumentation {

	@Autowired
	private UserRepository userRepository;

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void userGet() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/user").param("page", "0"))
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
					fieldWithPath("content").type(ARRAY).description("User itself")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void userCreate() throws Exception {
		//@formatter:off
		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", "newUsername")
				.put("password", "newPassword")
				.put("email", "newUsername@email.com")
				.put("authorities", Arrays.asList("USER", "ADMIN"));

		final RequestBuilder createRequest = post("/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc()
			.perform(createRequest)
			.andExpect(status().isCreated())
			.andDo(docHandler().document(
				requestFields(
					fieldWithPath("username").description("Username to use"),
					fieldWithPath("password").description("Password to use"),
					fieldWithPath("email").description("Email to use"),
					fieldWithPath("locale").type(STRING).optional().description("Optional locale in english (e.g. 'german')"),
					fieldWithPath("authorities").type(ARRAY).description("Authorities to grant")
				),
				responseFieldsForUserModel()
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void userDelete() throws Exception {
		//@formatter:off
		final Optional<User> user = userRepository.findUserByUsernameIgnoreCase(USER_USERNAME);
		mockMvc()
			.perform(delete("/user/{id}", user.get().getId()))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				pathParameters(
					parameterWithName("id").description("Identifier of the user")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void userUpdate() throws Exception {
		//@formatter:off
		final JsonBuilder userUpdate = jsonBuilder()
				.put("email", "newAddress@email.com")
				.put("locale", "german")
				.put("authorities", Arrays.asList("USER", "ADMIN"));

		final Optional<User> oldUser = userRepository.findUserByUsernameIgnoreCase(USER_USERNAME);
		final RequestBuilder request = put("/user/{id}", oldUser.get().getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(userUpdate.build()));

		mockMvc()
			.perform(request)
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				requestFields(
					fieldWithPath("email").description("The email address"),
					fieldWithPath("locale").description("Locale of the user"),
					fieldWithPath("authorities").type(ARRAY).description("Granted authorities")
				),
				responseFieldsForUserModel()
				));
		//@formatter:on
	}

	private ResponseFieldsSnippet responseFieldsForUserModel() {
		//@formatter:off
		return responseFields(
			fieldWithPath("id").description("The identifier of the current user"),
			fieldWithPath("username").description("The username"),
			fieldWithPath("email").description("The email address"),
			fieldWithPath("locale").description("Locale of the user"),
			fieldWithPath("authorities").type(ARRAY).description("Granted authorities")
		);
		//@formatter:on
	}

}
