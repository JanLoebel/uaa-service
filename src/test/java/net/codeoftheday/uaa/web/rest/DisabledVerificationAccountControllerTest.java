package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_ID;
import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_SECRET;
import static net.codeoftheday.uaa.testutils.TestUserComponent.USER_USERNAME;
import static net.codeoftheday.uaa.testutils.utils.EncodeUtils.encodeBase64;
import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.email.EmailService;
import net.codeoftheday.uaa.testutils.integration.AbstractMockMvcIntegrationTest;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;
import net.codeoftheday.uaa.user.UserRepository;

/**
 * Will test if verification of accounts is disabled, the use can login without activation and that he will not receive
 * a email notification.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles({ "dev", "test", "disabledverification" })
@Transactional // After each test-method the data will be rolled back.
public class DisabledVerificationAccountControllerTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@MockBean
	private EmailService emailServiceMock;

	@Test
	public void activationByCodeFailWithDisabledVerification() throws Exception {
		//@formatter:off
		final Optional<User> createdUser = userRepository.findUserByUsernameIgnoreCase(USER_USERNAME);

		final RequestBuilder activateRequest = get("/account/{id}/activate", createdUser.get().getId())
				.param("code", "anyVerficationCode");
		mockMvc()
			.perform(activateRequest)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error_key", is("ACCOUNT_ACTIVATION_DISABLED")));
		//@formatter:on
	}

	@Test
	public void afterRegisterAccountIsDirectlyActivated() throws Exception {
		//@formatter:off
		final String username = "AnyNewUserName";
		final String password = "AnyNewUserNamePassword";
		final String email = "anyNewUserName@Email.com";

		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", username)
				.put("password", password)
				.put("email", email);

		final RequestBuilder registerRequest = post("/account/register")
				.contentType(APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc()
			.perform(registerRequest)
			.andDo(print())
			.andExpect(status().isCreated());

		// Account created, so directly get a token
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + encodeBase64(CLIENT_ID, CLIENT_SECRET));

		final RequestBuilder authRequest = post("/oauth/token")
				.param("username", username)
				.param("password", password)
				.param("grant_type", "password")
				.headers(headers);

		mockMvc()
			.perform(authRequest)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token_type", is("bearer")))
			.andExpect(jsonPath("$.scope", is("myScope")))
			.andExpect(jsonPath("$.jti").isNotEmpty())
			.andExpect(jsonPath("$.expires_in").isNotEmpty())
			.andExpect(jsonPath("$.refresh_token").isNotEmpty())
			.andExpect(jsonPath("$.access_token").isNotEmpty());

		// No email will be send
		verifyZeroInteractions(emailServiceMock);

		//@formatter:on
	}
}
