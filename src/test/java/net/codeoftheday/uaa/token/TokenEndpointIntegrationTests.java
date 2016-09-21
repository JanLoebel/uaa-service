package net.codeoftheday.uaa.token;

import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_ID;
import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_SECRET;
import static net.codeoftheday.uaa.testutils.TestUserComponent.ADMIN_PASSWORD;
import static net.codeoftheday.uaa.testutils.TestUserComponent.ADMIN_USERNAME;
import static net.codeoftheday.uaa.testutils.utils.EncodeUtils.encodeBase64;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.RequestBuilder;

import net.codeoftheday.uaa.testutils.integration.AbstractMockMvcIntegrationTest;

@RunWith(SpringRunner.class)
public class TokenEndpointIntegrationTests extends AbstractMockMvcIntegrationTest {

	@Test
	public void tokenEndpointWithCorrectUserCredentials() throws Exception {
		//@formatter:off
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + encodeBase64(CLIENT_ID, CLIENT_SECRET));

		final RequestBuilder request = post("/oauth/token")
				.param("username", ADMIN_USERNAME)
				.param("password", ADMIN_PASSWORD)
				.param("grant_type", "password")
				.headers(headers);

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isOk())
			// Bearer token through user authentification
			.andExpect(jsonPath("$.token_type", is("bearer")))
			// Scope of the client
			.andExpect(jsonPath("$.scope", is("myScope"))).andExpect(jsonPath("$.jti").exists())
			.andExpect(jsonPath("$.expires_in").exists()).andExpect(jsonPath("$.refresh_token").exists())
			.andExpect(jsonPath("$.access_token").exists());
		//@formatter:on
	}

	@Test
	public void tokenEndpointWithWrongUserCredentials() throws Exception {
		//@formatter:off
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + encodeBase64(CLIENT_ID, CLIENT_SECRET));

		final RequestBuilder request = post("/oauth/token")
				.param("username", "NO_I_DONT_EXISTS")
				.param("password", "AnyPassword")
				.param("grant_type", "password")
				.headers(headers);

		mockMvc()
			.perform(request)
			.andDo(print())
			.andExpect(status().isBadRequest());
		//@formatter:on
	}
}
