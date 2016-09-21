package net.codeoftheday.uaa.util;

import org.apache.commons.lang3.RandomStringUtils;

public final class CodeGeneratorUtils {

	private CodeGeneratorUtils() {
	}

	public static String generateActivationCode() {
		return RandomStringUtils.randomAlphanumeric(16);
	}

	public static String generatePasswordResetCode() {
		return RandomStringUtils.randomAlphanumeric(16);
	}

	public static String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}
}
