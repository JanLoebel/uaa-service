package net.codeoftheday.uaa.config.security;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.Http401AuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private Environment env;

	@Override
	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		// @formatter:off
      httpSecurity
         .authorizeRequests()
            // Allow following urls to be called without authentification
            .antMatchers(getUrlsWithoutAuth()).permitAll()

            // All other urls are protected
            .anyRequest().authenticated()
         .and()
         
         // Disable BasicAuth to ensure only valid clients can access the API
         .httpBasic().disable()

         // Disable Cross Site Request Forgery
         .csrf().disable()

         // Enable FrameOptions for SameOrigin for H2
         .headers().frameOptions().sameOrigin().httpStrictTransportSecurity().disable().and()
         
         // Only statless sessions
         .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
         .and()
         
         // Exception while authentication will be returned as 401
         .exceptionHandling().authenticationEntryPoint(securityException401EntryPoint());
      // @formatter:off
   }

   private String[] getUrlsWithoutAuth() {
      final List<String> urls = new ArrayList<>();
      
      // Default URLs
      urls.addAll(asList(
         //@formatter:off
         "/account/*/activate",
         "/account/register",
         "/account/password/reset",
         "/account/password/reset/confirm",
         
         // Server defaults
         "/images/**",
         "/favicon.ico"
         //@formatter:on
		));

		if (env.acceptsProfiles("test")) {
			// H2 Console
			urls.addAll(asList("/h2-console/**"));
		}

		if (env.acceptsProfiles("doc")) {
			urls.addAll(asList("/docs/**"));
		}

		return urls.toArray(new String[urls.size()]);
	}

	@Bean
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Override
	protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public Http401AuthenticationEntryPoint securityException401EntryPoint() {
		return new Http401AuthenticationEntryPoint("401 Authentification Exception");
	}
}
