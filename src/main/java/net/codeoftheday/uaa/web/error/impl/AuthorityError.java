package net.codeoftheday.uaa.web.error.impl;

import net.codeoftheday.uaa.web.error.ErrorMessage;

public enum AuthorityError {
	//@formatter:off
   
   @ErrorMessage("Given authority already exists.")
   AUTHORITY_DUPLICATE;
   
   //@formatter:on
}
