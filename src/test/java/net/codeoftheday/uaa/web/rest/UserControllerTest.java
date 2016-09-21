package net.codeoftheday.uaa.web.rest;

import static net.codeoftheday.uaa.testutils.TestUserComponent.USER_USERNAME;
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

import java.util.Arrays;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
public class UserControllerTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private UserRepository userRepository;

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void getUserIsPageable() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/user"))
			.andDo(print())
			.andExpect(status().isOk())

			// has pageable attributes
			.andExpect(jsonPath("$.totalPages").isNotEmpty())
			.andExpect(jsonPath("$.size").isNotEmpty())
			.andExpect(jsonPath("$.totalElements").isNotEmpty())
			.andExpect(jsonPath("$.first").isNotEmpty())
			.andExpect(jsonPath("$.last").isNotEmpty())

			// Check content exists
			.andExpect(jsonPath("$.content").isNotEmpty());
		//@formatter:off
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void deleteUser() throws Exception {
		//@formatter:off
		final Optional<User> user = userRepository.findUserByUsernameIgnoreCase(USER_USERNAME);
		mockMvc()
			.perform(delete("/user/{id}", user.get().getId()))
			.andDo(print())
			.andExpect(status().isOk());

		assertThat(userRepository.findUserByUsernameIgnoreCase(USER_USERNAME).isPresent()).isFalse();
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void updateUser() throws Exception {
		//@formatter:off
		final String newEmail = "newEmailer@Mailer.Com";

		final JsonBuilder userUpdate = jsonBuilder()
				.put("id", "NotSetableAtAll")
				.put("username", "NotUpdatable")
				.put("email", newEmail)
				.put("locale", "german")
				.put("authorities", Arrays.asList("USER", "ADMIN"));

		final Optional<User> oldUser = userRepository.findUserByUsernameIgnoreCase(USER_USERNAME);
		final RequestBuilder request = put("/user/{id}", oldUser.get().getId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(userUpdate.build()));

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isOk())

			// Not updatable values
			.andExpect(jsonPath("$.id", is(oldUser.get().getId())))
			.andExpect(jsonPath("$.username", is(oldUser.get().getUsername())))

			// Updated values
			.andExpect(jsonPath("$.email", is(newEmail.toLowerCase())))
			.andExpect(jsonPath("$.locale", is("german")))
			.andExpect(jsonPath("$.authorities", hasSize(2)))
			.andExpect(jsonPath("$.authorities", hasItems("ADMIN", "USER")));
		//@formatter:on
	}

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void createUser() throws Exception {
		//@formatter:off
		final JsonBuilder userToCreate = jsonBuilder()
				.put("id", "NotSetableAtAll")
				.put("username", "ByAdminCreatedUser")
				.put("password", "newPassword")
				.put("email", "created@admin.user")
				.put("activationCode", "012345678912")
				.put("authorities", Arrays.asList("USER", "ADMIN"));

		final RequestBuilder createRequest = post("/user")
				.contentType(MediaType.APPLICATION_JSON)
				.content(json(userToCreate.build()));

		mockMvc()
			.perform(createRequest)
			.andDo(print())
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").isNotEmpty())
			.andExpect(jsonPath("$.activationCode", is("012345678912")))
			.andExpect(jsonPath("$.authorities", hasSize(2)))
			.andExpect(jsonPath("$.authorities", hasItems("ADMIN", "USER")));
		//@formatter:on
	}
}
