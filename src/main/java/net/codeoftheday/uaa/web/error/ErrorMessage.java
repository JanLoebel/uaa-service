package net.codeoftheday.uaa.web.error;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ErrorMessage {

	String value();
}
