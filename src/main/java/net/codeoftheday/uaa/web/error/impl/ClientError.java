package net.codeoftheday.uaa.web.error.impl;

import net.codeoftheday.uaa.web.error.ErrorMessage;

public enum ClientError {
	//@formatter:off

   @ErrorMessage("Given client already exists.")
   CLIENT_DUPLICATE,

   @ErrorMessage("Given client does not exist.")
   CLIENT_NOT_FOUND,

   @ErrorMessage("Given client_id is not allowed to be empty.")
   CLIENT_ID_EMPTY;

   //@formatter:on

}
