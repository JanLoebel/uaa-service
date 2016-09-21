package net.codeoftheday.uaa.config.security.jwt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.store.KeyStoreKeyFactory;

import net.codeoftheday.uaa.config.UaaProperties;

@Configuration
public class JwtTokenConfig {

	@Autowired
	private UaaProperties uaaProperties;

	@Autowired
	private ResourceLoader resourceLoader;

	@Bean
	public TokenStore tokenStore() {
		return new JwtTokenStore(jwtTokenEnhancer());
	}

	@Bean
	public JwtAccessTokenConverter jwtTokenEnhancer() {
		final Resource resource = resourceLoader.getResource(uaaProperties.getJwt().getKeystore().getPath());

		final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource,
				uaaProperties.getJwt().getKeystore().getPassword().toCharArray());
		final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
		converter.setKeyPair(keyStoreKeyFactory.getKeyPair(uaaProperties.getJwt().getKeystore().getKeypair()));
		return converter;
	}

}
