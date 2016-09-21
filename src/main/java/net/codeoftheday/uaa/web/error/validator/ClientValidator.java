package net.codeoftheday.uaa.web.error.validator;

import static net.codeoftheday.uaa.web.error.ErrorResponse.errorBuilder;
import static net.codeoftheday.uaa.web.error.ErrorResponseException.errorResponse;
import static net.codeoftheday.uaa.web.error.validator.ClientValidator.ClientError.CLIENT_ID_EMPTY;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import net.codeoftheday.uaa.domain.dto.ClientDto;
import net.codeoftheday.uaa.web.error.ErrorMessage;

@Component
public class ClientValidator {

	public void validate(final ClientDto clientDto) {
		validateClientId(clientDto.getClientId());
	}

	private void validateClientId(final String clientId) {
		// Validate password is given
		if (StringUtils.isBlank(clientId)) {
			throw errorResponse(errorBuilder().errorKey(CLIENT_ID_EMPTY));
		}
	}

	public enum ClientError {
		//@formatter:off
		@ErrorMessage("Given client_id is empty.")
		CLIENT_ID_EMPTY
		//@formatter:on
	}

}
