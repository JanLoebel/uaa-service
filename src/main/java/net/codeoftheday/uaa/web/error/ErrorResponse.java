package net.codeoftheday.uaa.web.error;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import lombok.Getter;
import lombok.ToString;

@ToString
public class ErrorResponse implements Serializable {
	private static final long serialVersionUID = -6906302665748898428L;

	@Getter
	private String errorKey = "GENERAL_ERROR";

	@Getter
	private String message = "Error";

	@Getter
	private HttpStatus httpStatus = HttpStatus.BAD_REQUEST;

	public static ErrorResponse errorBuilder() {
		return new ErrorResponse();
	}

	public ErrorResponse errorKey(final Enum<?> errorKey) {
		this.errorKey = errorKey.name().toUpperCase();
		this.message = extractErrorMessage(errorKey);
		return this;
	}

	public ErrorResponse errorKey(final String errorKey) {
		this.errorKey = errorKey;
		return this;
	}

	public ErrorResponse message(final String message) {
		this.message = message;
		return this;
	}

	public ErrorResponse httpStatus(final HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
		return this;
	}

	public ResponseEntity<?> build() {
		final Map<String, String> body = new HashMap<>();
		body.put("error_key", this.errorKey.toUpperCase());
		body.put("error_message", this.message);

		return ResponseEntity.status(this.httpStatus).body(body);
	}

	private String extractErrorMessage(final Enum<?> errorMessage) {
		try {
			final ErrorMessage annotation = errorMessage.getClass().getField(errorMessage.name())
					.getAnnotation(ErrorMessage.class);
			if (annotation != null) {
				return annotation.value();
			}
		} catch (@SuppressWarnings("squid:S1166") final NoSuchFieldException | SecurityException e) {
			// Just ignore those
		}
		return null;
	}
}
