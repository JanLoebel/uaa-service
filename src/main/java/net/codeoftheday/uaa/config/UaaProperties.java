package net.codeoftheday.uaa.config;

import java.util.Locale;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties(prefix = "uaa", ignoreUnknownFields = false)
public class UaaProperties {

	@Getter
	@Setter
	private String baseUrl = "";

	@Getter
	@Setter
	private String bootstrapFile;

	@Getter
	@Setter
	private String templatePath;

	@Getter
	private final ClientDefault clientDefault = new ClientDefault();

	@Getter
	private final Mail mail = new Mail();

	@Getter
	private final Account account = new Account();

	@Getter
	private final Jwt jwt = new Jwt();

	public static class ClientDefault {
		@Getter
		@Setter
		private Integer accessTokenValiditySeconds = 3000;

		@Getter
		@Setter
		private String authorizedGrantTypes = "implicit,refresh_token,password,authorization_code";

		@Getter
		@Setter
		private String autoApprove = "true";
	}

	public static class Mail {
		@Getter
		@Setter
		private String from = "uaa@localhost.local";
	}

	public static class Account {
		@Getter
		@Setter
		private boolean verification = true;

		@Getter
		@Setter
		private int passwordResetCodeValidityMinutes = 3600;

		@Getter
		@Setter
		private String defaultLocale = Locale.ENGLISH.getDisplayLanguage(Locale.ENGLISH);

		@Getter
		@Setter
		private Password password = new Password();

		@Getter
		@Setter
		private Username username = new Username();

		@Getter
		@Setter
		private Email email = new Email();

	}

	public static class Email {
		@Getter
		@Setter
		private int minLength = 4;

		@Getter
		@Setter
		private int maxLength = 100;
	}

	public static class Username {
		@Getter
		@Setter
		private int minLength = 3;

		@Getter
		@Setter
		private int maxLength = 50;

		@Getter
		@Setter
		private String regex = "^[a-zA-Z0-9]*$";
	}

	public static class Password {
		@Getter
		@Setter
		private int minLength = 4;

		@Getter
		@Setter
		private int maxLength = 100;

		@Getter
		@Setter
		private String regex = null;
	}

	public static class Jwt {

		@Getter
		private final Keystore keystore = new Keystore();

		public static class Keystore {
			@Getter
			@Setter
			private String path;

			@Getter
			@Setter
			private String password;

			@Getter
			@Setter
			private String keypair;
		}
	}

}
