package net.codeoftheday.uaa.documentation;

import static net.codeoftheday.uaa.testutils.TestUserComponent.USER_EMAIL;
import static net.codeoftheday.uaa.testutils.TestUserComponent.USER_USERNAME;
import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.JsonFieldType.ARRAY;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.testutils.integration.AbstractDocumentation;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;
import net.codeoftheday.uaa.user.UserRepository;

@RunWith(SpringRunner.class)
public class AccountControllerDocumentation extends AbstractDocumentation {

	@Autowired
	private UserRepository userRepository;

	@MockBean
	private JavaMailSender mailSenderMock;

	@Before
	@Override
	public void setUp() {
		super.setUp();

		// Setup MailSenderMock
		final Session nullSession = null;
		when(mailSenderMock.createMimeMessage()).thenReturn(new MimeMessage(nullSession));
	}

	@Test
	public void accountRegisterNewUser() throws Exception {
		//@formatter:off
		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", "myUsername")
				.put("password", "myPassword")
				.put("email", "MyEmail@email.com")
				.put("locale", "german");

		final RequestBuilder request = post("/account/register")
				.contentType(APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc()
			.perform(request)
			.andExpect(status().isCreated())
			.andDo(docHandler().document(
				requestFields(
					fieldWithPath("username").description("The username"),
					fieldWithPath("password").description("The password"),
					fieldWithPath("email").description("The email address"),
					fieldWithPath("locale").optional().description("Optional locale in english (e.g. 'german')")
				)
			));
		//@formatter:on
	}

	@Test
	@WithMockUser(username = USER_USERNAME, authorities = "USER")
	public void accountCurrentUserInformation() throws Exception {
		//@formatter:off
		final RequestBuilder request = get("/account")
				.contentType(APPLICATION_JSON);

		mockMvc()
		.perform(request)
		.andExpect(status().isOk())
		.andDo(docHandler().document(
			responseFields(
				fieldWithPath("id").description("The identifier of the current user"),
				fieldWithPath("username").description("The username"),
				fieldWithPath("email").description("The email address"),
				fieldWithPath("locale").description("Optional locale in english (e.g. 'german')"),
				fieldWithPath("authorities").type(ARRAY).description("Granted authorities")
			)
		));
		//@formatter:on
	}

	@Test
	@WithMockUser(username = USER_USERNAME, authorities = "USER")
	public void accountUpdateUserInformation() throws Exception {
		//@formatter:off
		final JsonBuilder attributesToUpdate = jsonBuilder()
				.put("password", "MyNewPassword")
				.put("locale", "german");

		final RequestBuilder request = put("/account")
				.contentType(APPLICATION_JSON)
				.content(json(attributesToUpdate.build()));

		mockMvc()
		.perform(request)
		.andExpect(status().isOk())
		.andDo(docHandler().document(
			requestFields(
				fieldWithPath("password").optional().description("New password if the user want to update it."),
				fieldWithPath("locale").optional().description("New locale in english (e.g. 'german') if the user want to update it")
			)
		));
		//@formatter:on
	}

	@Test
	public void accountActivateNewUser() throws Exception {
		final User registeredUser = createUser();
		//@formatter:off
		final RequestBuilder request = get("/account/{id}/activate", registeredUser.getId())
				.param("code", registeredUser.getActivationCode());
		mockMvc()
			.perform(request)
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				pathParameters(
					parameterWithName("id").description("Identifier of the user")
				),
				requestParameters(
					parameterWithName("code").description("Activation code of the user")
				)
			));
		//@formatter:on
	}

	@Test
	public void accountTriggerPasswordReset() throws Exception {
		//@formatter:off
		final RequestBuilder request = post("/account/password/reset")
				.param("email", USER_EMAIL);
		mockMvc()
			.perform(request)
			.andExpect(status().isOk())
			.andDo(docHandler().document(
					requestParameters(
						parameterWithName("email").description("Email address of the user to reset password.")
					)
				));
		//@formatter:on
	}

	@Test
	public void accountConfirmPasswordReset() throws Exception {
		final User user = triggerResetUserPassword();
		//@formatter:off

		final RequestBuilder request = post("/account/password/reset/confirm")
				.param("email", USER_EMAIL)
				.param("code", user.getPasswordResetCode())
				.param("password", "anyNewPassword");

		mockMvc()
			.perform(request)
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				requestParameters(
					parameterWithName("email").description("Email address of the user to reset password."),
					parameterWithName("code").description("Password reset code of the user"),
					parameterWithName("password").description("New password of the user")
				)
			));
		//@formatter:on
	}

	private User createUser() throws Exception {
		//@formatter:off
		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", "activateMe")
				.put("password", "activateMe")
				.put("email", "activateMe@activate.com");
		final RequestBuilder createRequest = post("/account/register")
				.contentType(APPLICATION_JSON)
				.content(json(userToCreate.build()));
		mockMvc()
			.perform(createRequest)
			.andExpect(status().isCreated());

		//@formatter:on
		return userRepository.findUserByUsernameIgnoreCase("activateMe").get();
	}

	private User triggerResetUserPassword() throws Exception {
		//@formatter:off
		final RequestBuilder resetRequest = post("/account/password/reset")
				.param("email", USER_EMAIL);
		mockMvc()
			.perform(resetRequest)
			.andExpect(status().isOk());
		//@formatter:on
		return userRepository.findUserByEmailIgnoreCase(USER_EMAIL).get();
	}

}
