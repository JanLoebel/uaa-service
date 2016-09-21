package net.codeoftheday.uaa.config.security;

import static org.junit.Assert.assertTrue;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import net.codeoftheday.uaa.UaaApp;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { UaaApp.class }, webEnvironment = RANDOM_PORT)
@ActiveProfiles({ "dev", "test" })
@AutoConfigureTestDatabase
public class OAuth2ConfigTest {

	@Autowired
	private TokenStore tokenStore;

	@Test
	public void tokenStoreIsJwt() {
		assertTrue("Wrong token store type: " + tokenStore, tokenStore instanceof JwtTokenStore);
	}
}
