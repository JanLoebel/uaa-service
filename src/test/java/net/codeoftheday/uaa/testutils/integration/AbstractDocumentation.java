package net.codeoftheday.uaa.testutils.integration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.restdocs.JUnitRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.codeoftheday.uaa.UaaApp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UaaApp.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles({ "dev", "test" })
@AutoConfigureTestDatabase
@Transactional
public abstract class AbstractDocumentation {

	@Rule
	public JUnitRestDocumentation restDocumentation = new JUnitRestDocumentation("target/generated-snippets");

	@Autowired
	private WebApplicationContext context;

	@Autowired
	private ObjectMapper objectMapper;

	private RestDocumentationResultHandler documentationHandler;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		//@formatter:off
		this.documentationHandler = document("{method-name}",
				preprocessRequest(prettyPrint()),
				preprocessResponse(prettyPrint()));

		this.mockMvc = MockMvcBuilders
				.webAppContextSetup(this.context)
				.apply(springSecurity())
				.apply(documentationConfiguration(this.restDocumentation))
				.alwaysDo(document("{method-name}"))
				.build();
		//@formatter:ion
	}

	protected RestDocumentationResultHandler docHandler() {
		return this.documentationHandler;
	}

	protected MockMvc mockMvc() {
		return this.mockMvc;
	}

	protected String json(final Object value) {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (final JsonProcessingException e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
			return null;
		}
	}
}
