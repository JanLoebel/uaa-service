package net.codeoftheday.uaa.token;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import net.codeoftheday.uaa.testutils.integration.AbstractMockMvcIntegrationTest;

@RunWith(SpringRunner.class)
public class TokenKeyEndpointIntegrationTests extends AbstractMockMvcIntegrationTest {

	@Test
	public void tokenKeyEndpointExists() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/oauth/token_key"))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.alg").exists())
			.andExpect(jsonPath("$.value").exists());
		//@formatter:on
	}

}
