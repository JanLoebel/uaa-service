package net.codeoftheday.uaa.util.jsonfilter.impl.roles;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface JsonVisibleRoles {

	String roles();

}
