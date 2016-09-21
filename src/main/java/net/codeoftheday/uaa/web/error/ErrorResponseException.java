package net.codeoftheday.uaa.web.error;

import static com.google.common.base.Preconditions.checkNotNull;

public class ErrorResponseException extends RuntimeException {
	private static final long serialVersionUID = -4976337703540514406L;

	private final ErrorResponse errorResponse;

	public ErrorResponseException(final ErrorResponse errorResponse) {
		this.errorResponse = checkNotNull(errorResponse);
	}

	public static ErrorResponseException errorResponse(final ErrorResponse errorResponse) {
		return new ErrorResponseException(errorResponse);
	}

	public ErrorResponse getErrorResponse() {
		return errorResponse;
	}

	@Override
	public String getMessage() {
		return errorResponse.getMessage();
	}
}
