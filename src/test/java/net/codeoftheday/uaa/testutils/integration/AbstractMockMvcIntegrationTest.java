package net.codeoftheday.uaa.testutils.integration;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.codeoftheday.uaa.UaaApp;
import net.codeoftheday.uaa.config.UaaProperties;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = UaaApp.class, webEnvironment = RANDOM_PORT)
@ActiveProfiles({ "dev", "test" })
@AutoConfigureTestDatabase
public abstract class AbstractMockMvcIntegrationTest {

	@Autowired
	private UaaProperties uaaProperties;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private WebApplicationContext webApplicationContext;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
				// Add Spring Security for Token authentication and so ons
				.apply(springSecurity()).build();
	}

	@After
	public void tearDown() {
	}

	protected MockMvc mockMvc() {
		return this.mockMvc;
	}

	protected UaaProperties getUaaProperties() {
		return this.uaaProperties;
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
