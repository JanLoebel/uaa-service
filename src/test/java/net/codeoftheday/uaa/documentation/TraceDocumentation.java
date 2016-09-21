package net.codeoftheday.uaa.documentation;

import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import net.codeoftheday.uaa.testutils.integration.AbstractDocumentation;

@RunWith(SpringRunner.class)
public class TraceDocumentation extends AbstractDocumentation {

	@Test
	@WithMockUser(authorities = "ADMIN")
	public void traceEnable() throws Exception {
		//@formatter:off
		mockMvc()
			.perform(get("/user").param("trace", "on"))
			.andExpect(status().isOk());
		//@formatter:on
	}

}
