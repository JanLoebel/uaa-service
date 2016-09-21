package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.testutils.TestUserComponent.USER_EMAIL;
import static net.codeoftheday.uaa.testutils.TestUserComponent.USER_USERNAME;
import static net.codeoftheday.uaa.testutils.utils.jsonbuilder.DefaultJsonBuilder.jsonBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;
import java.util.Optional;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.transaction.annotation.Transactional;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.testutils.integration.AbstractMockMvcIntegrationTest;
import net.codeoftheday.uaa.testutils.utils.jsonbuilder.JsonBuilder;
import net.codeoftheday.uaa.user.UserRepository;

@RunWith(SpringRunner.class)
@Transactional // After each test-method the data will be rolled back.
public class AccountControllerTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private PasswordEncoder passwordEncoder;

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
	public void registerWithInvalidPassword() throws Exception {
		//@formatter:off
		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", USER_USERNAME)
				.put("password", "")
				.put("email", "anyNewEmail@mail.com");

		final RequestBuilder request = post("/account/register")
				.contentType(APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error_key", is("PASSWORD_EMPTY")));
		//@formatter:on
	}

	@Test
	public void sameUsernameAlreadyExists() throws Exception {
		//@formatter:off
		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", USER_USERNAME)
				.put("password", "anyPassword")
				.put("email", "anyNewEmail@mail.com");

		final RequestBuilder request = post("/account/register")
				.contentType(APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.error_key", is("ACCOUNT_DUPLICATE_USERNAME")));
		//@formatter:on
	}

	@Test
	public void validRegistrationReturnCorrectValues() throws Exception {
		//@formatter:off
		final String username = "AnyNewUserName";
		final String password = "AnyNewUserNamePassword";
		final String email = "anyNewUserName@Email.com";

		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", username)
				.put("password", password)
				.put("email", email);

		final RequestBuilder request = post("/account/register")
				.contentType(APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc().perform(request).andDo(print())
				// 201 CREATED is the correct return value
				.andExpect(status().isCreated())
				// id is returned
				.andExpect(jsonPath("$.id").isNotEmpty())
				// given username lower case
				.andExpect(jsonPath("$.username", is(username.toLowerCase())))
				// Password will not be returned
				.andExpect(jsonPath("$.password").doesNotExist())
				// One authority which is User
				.andExpect(jsonPath("$.authorities", hasSize(1))).andExpect(jsonPath("$.authorities", hasItem("USER")))
				// Email all lower case
				.andExpect(jsonPath("$.email", is(email.toLowerCase())))
				// activation code will not returned
				.andExpect(jsonPath("$.activationCode").doesNotExist())
				// default locale is english
				.andExpect(jsonPath("$.locale", is("english")));

		final Optional<User> createdUser = userRepository.findUserByUsernameIgnoreCase(username);
		assertThat(createdUser.get().getActivationCode()).isNotEmpty();

		// Validate received mail
		validateMail(createdUser.get().getEmail());
		//@formatter:on
	}

	@Test
	@WithMockUser(username = USER_USERNAME, authorities = "USER")
	public void updateUserDataWithLocale() throws Exception {
		//@formatter:off
		final JsonBuilder attributesToUpdate = jsonBuilder().put("locale", "german");

		final RequestBuilder request = put("/account")
				.contentType(APPLICATION_JSON)
				.content(json(attributesToUpdate.build()));

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.locale", is("german")));
		//@formatter:on
	}

	@Test
	@WithMockUser(username = USER_USERNAME, authorities = "USER")
	public void updateUserData() throws Exception {
		//@formatter:off
		final JsonBuilder attributesToUpdate = jsonBuilder()
				// Username is not changable
				.put("username", "newUserNameWillBeIgnored")
				// Email is not changable
				.put("email", "newEmailWillBeIgnored@test.com")
				// New password should be used
				.put("password", "MyNewPassword")
				// Should not be able to add authority
				.put("authorities", Arrays.asList("ADMIN", "USER"));

		final RequestBuilder request = put("/account").contentType(APPLICATION_JSON)
				.content(json(attributesToUpdate.build()));

		mockMvc().perform(request).andDo(print())
				// 200 OK for updated user
				.andExpect(status().isOk())
				// id is returned
				.andExpect(jsonPath("$.id").isNotEmpty())
				// username won't be changed
				.andExpect(jsonPath("$.username", is(USER_USERNAME)))
				// Password will still not be returned
				.andExpect(jsonPath("$.password").doesNotExist())
				// One authority which is User, but nothing changed
				.andExpect(jsonPath("$.authorities", hasSize(1))).andExpect(jsonPath("$.authorities", hasItem("USER")))
				// Email all lower case
				.andExpect(jsonPath("$.email", is(USER_EMAIL)))
				// activation code will not returned
				.andExpect(jsonPath("$.activationCode").doesNotExist());

		// Validate saved user for attributes which are not returned, like password
		final Optional<User> loadedUser = userRepository.findUserByUsernameIgnoreCase(USER_USERNAME);
		assertThat(passwordEncoder.matches("MyNewPassword", loadedUser.get().getPassword())).isTrue();
		//@formatter:on
	}

	@Test
	@WithMockUser(username = USER_USERNAME, authorities = "USER")
	public void getUserData() throws Exception {
		//@formatter:off
		final RequestBuilder request = get("/account");

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.username", is(USER_USERNAME)))
			.andExpect(jsonPath("$.locale").isNotEmpty())
			.andExpect(jsonPath("$.password").doesNotExist())
			.andExpect(jsonPath("$.activationCode").doesNotExist());
		//@formatter:on
	}

	@Test
	public void getUserDataWithoutAuthorization() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/account"))
			.andDo(print())
			.andExpect(status().isUnauthorized());
		//@formatter:on
	}

	@Test
	public void activationByCode() throws Exception {
		//@formatter:off
		final String username = "activateMe";

		// Create user
		final JsonBuilder userToCreate = jsonBuilder()
				.put("username", username)
				.put("password", "activateMe")
				.put("email", "activateMe@activate.com");

		final RequestBuilder createRequest = post("/account/register")
				.contentType(APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc()
			.perform(createRequest)
			.andDo(print())
			.andExpect(status().isCreated());

		// Get activation token and activate user
		final Optional<User> createdUser = userRepository.findUserByUsernameIgnoreCase(username);

		final RequestBuilder activateRequest = get("/account/{id}/activate", createdUser.get().getId())
				.param("code", createdUser.get().getActivationCode());
		mockMvc()
			.perform(activateRequest)
			.andDo(print())
			.andExpect(status().isOk());

		// Is user activated?
		final Optional<User> activatedUser = userRepository.findUserByUsernameIgnoreCase(username);
		assertThat(activatedUser.get().getActivationCode()).isNull();

		// Validate received mail
		validateMail(createdUser.get().getEmail());
		//@formatter:on
	}

	@Test
	public void emailResetSendPasswordEmail() throws Exception {
		//@formatter:off
		final User oldUser = userRepository.findUserByEmailIgnoreCase(USER_EMAIL).get();
		final String oldPassword = oldUser.getPassword();

		// Trigger reset
		final RequestBuilder resetRequest = post("/account/password/reset")
				.param("email", USER_EMAIL);
		mockMvc()
			.perform(resetRequest)
			.andDo(print())
			.andExpect(status().isOk());

		// Verify data set
		final User updatedUser = userRepository.findUserByEmailIgnoreCase(USER_EMAIL).get();
		assertThat(updatedUser.getPassword()).isNotEmpty();
		assertThat(updatedUser.getPassword()).isNotEqualTo(oldPassword);
		assertThat(updatedUser.getPasswordResetCode()).isNotEmpty();
		assertThat(updatedUser.getPasswordResetTimestamp()).isNotNull();

		// Verify email was received
		validateMail(USER_EMAIL);
		//@formatter:on
	}

	@Test
	public void unkownEmailAlsoReturn200() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(post("/account/password/reset")
					.param("email", "IWasNeverRegistered@mail.com"))
			.andExpect(status().isOk());
		//@formatter:on
	}

	@Test
	public void newPasswordWithValidTokenSuccess() throws Exception {
		//@formatter:off
		// Start reset process and request token
		final RequestBuilder resetRequest = post("/account/password/reset")
				.param("email", USER_EMAIL);
		mockMvc()
			.perform(resetRequest)
			.andDo(print())
			.andExpect(status().isOk());
		final String passwordResetToken = userRepository.findUserByEmailIgnoreCase(USER_EMAIL).get()
				.getPasswordResetCode();

		// Set new password
		final String newPassword = "MyVerySecretPassword";
		final RequestBuilder setPasswordRequest = post("/account/password/reset/confirm")
				.param("email", USER_EMAIL)
				.param("code", passwordResetToken)
				.param("password", newPassword);
		mockMvc()
			.perform(setPasswordRequest)
			.andDo(print())
			.andExpect(status().isOk());

		// Validate new password was set
		final User user = userRepository.findUserByEmailIgnoreCase(USER_EMAIL).get();
		assertThat(user.getPasswordResetCode()).isNull();
		assertThat(user.getPasswordResetTimestamp()).isNull();
		assertThat(passwordEncoder.matches(newPassword, user.getPassword())).isTrue();
		//@formatter:on
	}

	private void validateMail(final String recipient) throws Exception {
		// Captor send message
		final ArgumentCaptor<MimeMessage> mimeMessageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
		verify(mailSenderMock, timeout(5000)).send(mimeMessageCaptor.capture());

		// Validate received email
		final MimeMessage email = mimeMessageCaptor.getValue();
		assertThat(email.getRecipients(Message.RecipientType.TO)[0].toString()).isEqualTo(recipient);
		assertThat(email.getSubject()).isNotEmpty();
		assertThat(email.getContent().toString()).isNotEmpty();
	}

}
