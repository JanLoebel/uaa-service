package net.codeoftheday.uaa.documentation;

import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_ID;
import static net.codeoftheday.uaa.testutils.TestClientComponent.CLIENT_SECRET;
import static net.codeoftheday.uaa.testutils.TestUserComponent.ADMIN_PASSWORD;
import static net.codeoftheday.uaa.testutils.TestUserComponent.ADMIN_USERNAME;
import static net.codeoftheday.uaa.testutils.utils.EncodeUtils.encodeBase64;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.payload.ResponseFieldsSnippet;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MvcResult;

import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.ReadContext;

import net.codeoftheday.uaa.testutils.integration.AbstractDocumentation;

@RunWith(SpringRunner.class)
public class TokenDocumentation extends AbstractDocumentation {

	@Test
	public void tokenRetrievePublicKey() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/oauth/token_key"))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				responseFields(
					fieldWithPath("alg").description("The algorithm of the public key"),
					fieldWithPath("value").description("The public key")
				)
			));
		//@formatter:on
	}

	@Test
	public void tokenLogin() throws Exception {
		//@formatter:off
		final HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + encodeBase64(CLIENT_ID, CLIENT_SECRET));

		mockMvc()
			.perform(post("/oauth/token")
				.param("username", ADMIN_USERNAME)
				.param("password", ADMIN_PASSWORD)
				.param("grant_type", "password")
				.headers(headers))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				requestHeaders(
					headerWithName("Authorization").description("Basic auth credentials for the client")
				),
				requestParameters(
					parameterWithName("username").description("The username to create token"),
					parameterWithName("password").description("The password to create token"),
					parameterWithName("grant_type").description("Type of the token to create")
				),
				responseFieldsForTokenModel()
			));
		//@formatter:on
	}

	private ResponseFieldsSnippet responseFieldsForTokenModel() {
		//@formatter:off
		return responseFields(
			fieldWithPath("token_type").description("Type of the given token"),
			fieldWithPath("scope").description("Scope of the given token"),
			fieldWithPath("expires_in").description("Milliseconds till the token expires"),
			fieldWithPath("access_token").description("The access token itself"),
			fieldWithPath("refresh_token").description("The refresh token"),
			fieldWithPath("jti").description("JWT Identifier")
		);
		//@formatter:on
	}

	@Test
	public void tokenRefresh() throws Exception {
		//@formatter:off

		// Login
		final MvcResult loginResult = mockMvc()
			.perform(post("/oauth/token")
				.param("username", ADMIN_USERNAME)
				.param("password", ADMIN_PASSWORD)
				.param("grant_type", "password")
				.header("Authorization", "Basic " + encodeBase64(CLIENT_ID, CLIENT_SECRET)))
			.andExpect(status().isOk())
			.andReturn();
		final ReadContext ctx = JsonPath.parse(loginResult.getResponse().getContentAsString());
		final String refreshToken = ctx.read("$.refresh_token");

		// Refresh token
		mockMvc()
			.perform(post("/oauth/token")
				.param("refresh_token", refreshToken)
				.param("grant_type", "refresh_token")
				.header("Authorization", "Basic " + encodeBase64(CLIENT_ID, CLIENT_SECRET)))
			.andExpect(status().isOk())
			.andDo(docHandler().document(
				requestHeaders(
					headerWithName("Authorization").description("Basic auth credentials for the client")
				),
				requestParameters(
					parameterWithName("grant_type").description("Type of the token to create"),
					parameterWithName("refresh_token").description("Refresh token from the last token")
				),
				responseFieldsForTokenModel()
			));
		//@formatter:on
	}

}
