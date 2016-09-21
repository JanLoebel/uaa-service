package net.codeoftheday.uaa.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.codeoftheday.uaa.web.error.ErrorResponseException;

@RestControllerAdvice
public class ErrorResponseExceptionAdvisor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ErrorResponseExceptionAdvisor.class);

	@ExceptionHandler(ErrorResponseException.class)
	public ResponseEntity<?> handleErrorResponse(final ErrorResponseException errorResponse) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Handle ErrorResponse -> {}", errorResponse.getErrorResponse());
		}

		return errorResponse.getErrorResponse().build();
	}

}
