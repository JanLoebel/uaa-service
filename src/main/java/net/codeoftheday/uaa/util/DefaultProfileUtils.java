package net.codeoftheday.uaa.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.core.env.SimpleCommandLinePropertySource;

public final class DefaultProfileUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger(DefaultProfileUtils.class);

	private static final String DEFAULT_PROFILE = "dev";
	private static final String SPRING_PROFILES_ACTIVE = "spring.profiles.active";
	private static final String ENV_SPRING_PROFILES_ACTIVE = "SPRING_PROFILES_ACTIVE";

	private DefaultProfileUtils() {
	}

	public static void addDefaultProfile(final SpringApplication app, final String... args) {
		if (argumentsDoesNotContainProfile(args) && systemPropertyDoesNotContainProfile()
				&& envDoesNotContainProfile()) {
			LOGGER.info("Could not found any set profile, so will add {}.", DEFAULT_PROFILE);
			app.setAdditionalProfiles(DEFAULT_PROFILE);
		}
	}

	private static boolean argumentsDoesNotContainProfile(final String... args) {
		final SimpleCommandLinePropertySource argsSource = new SimpleCommandLinePropertySource(args);
		return !argsSource.containsProperty(SPRING_PROFILES_ACTIVE);
	}

	private static boolean systemPropertyDoesNotContainProfile() {
		return System.getProperty(SPRING_PROFILES_ACTIVE) == null;
	}

	private static boolean envDoesNotContainProfile() {
		return System.getenv(ENV_SPRING_PROFILES_ACTIVE) == null;
	}

}
