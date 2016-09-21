package net.codeoftheday.uaa.web.error.impl;

import net.codeoftheday.uaa.web.error.ErrorMessage;

public enum AccountError {
	//@formatter:off
   
   @ErrorMessage("Given username is already in use.")
   ACCOUNT_DUPLICATE_USERNAME,
   
   @ErrorMessage("Given email is already in use.")
   ACCOUNT_DUPLICATE_EMAIL,
   
   @ErrorMessage("Given user does not exist.")
   ACCOUNT_NOT_FOUND,
   
   @ErrorMessage("Account activation is disabled.")
   ACCOUNT_ACTIVATION_DISABLED,
   
   @ErrorMessage("Given email or password reset code is invalid.")
   ACCOUNT_PASSWORD_RESET_INVALID,
   
   @ErrorMessage("Password reset is to old, please try to reset it again.")
   ACCOUNT_PASSWORD_RESET_OUTDATED;
   
   //@formatter:on
}
