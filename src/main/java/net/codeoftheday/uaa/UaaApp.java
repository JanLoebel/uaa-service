package net.codeoftheday.uaa;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableAsync;

import net.codeoftheday.uaa.bootstrap.BootstrapHandler;
import net.codeoftheday.uaa.config.UaaProperties;
import net.codeoftheday.uaa.util.DefaultProfileUtils;

@SpringBootApplication
@EnableAsync
@EnableConfigurationProperties({ UaaProperties.class })
public class UaaApp implements CommandLineRunner {
	private static final Logger LOGGER = LoggerFactory.getLogger(UaaApp.class);

	@Autowired
	private Environment env;

	@Autowired
	private BootstrapHandler bootstrapHandler;

	public static void main(final String... args) {
		final SpringApplication app = new SpringApplication(UaaApp.class);

		// If no profile is set, 'dev' will be used.
		DefaultProfileUtils.addDefaultProfile(app, args);

		app.run(args);
	}

	@Override
	public void run(final String... args) throws Exception {
		LOGGER.info("Running with active profiles: '{}'", Arrays.toString(env.getActiveProfiles()));

		bootstrapHandler.setup();
	}

}
