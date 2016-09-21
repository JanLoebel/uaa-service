package net.codeoftheday.uaa.email;

import static java.util.Locale.ENGLISH;
import static java.util.Locale.GERMAN;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import net.codeoftheday.uaa.domain.User;
import net.codeoftheday.uaa.testutils.integration.AbstractMockMvcIntegrationTest;

@RunWith(SpringRunner.class)
public class EmailTemplateServiceTest extends AbstractMockMvcIntegrationTest {

	@Autowired
	private EmailTemplateService underTest;

	@Test
	public void processActivationMailInGerman() {
		// Build user with necessary attributes
		final User user = new User();
		user.setId("MyId");
		user.setActivationCode("MyActivationCode");
		user.setEmail("MyUser@Email.com");
		user.setLocale(GERMAN.getDisplayLanguage(ENGLISH));

		final String content = underTest.processActivationMail(user);
		System.out.println(content);
		assertThat(content, not(containsString("${")));
	}

	@Test
	public void processActivationMailDoesNotLeaveAnyPlaceholder() {
		// Build user with necessary attributes
		final User user = new User();
		user.setId("MyId");
		user.setActivationCode("MyActivationCode");
		user.setEmail("MyUser@Email.com");
		user.setLocale(ENGLISH.getDisplayLanguage(ENGLISH));

		final String content = underTest.processActivationMail(user);
		assertThat(content, not(containsString("${")));
	}

	@Test
	public void processResetPasswordMailDoesNotLeaveAnyPlaceholder() {
		// Build user with necessary attributes
		final User user = new User();
		user.setId("MyId");
		user.setPasswordResetCode("MyPasswordResetCode");
		user.setEmail("MyUser@Email.com");
		user.setLocale(ENGLISH.getDisplayLanguage(ENGLISH));

		final String content = underTest.processPasswordResetMail(user);
		assertThat(content, not(containsString("${")));
	}
}
