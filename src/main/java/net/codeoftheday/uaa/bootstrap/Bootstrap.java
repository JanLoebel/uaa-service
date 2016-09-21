package net.codeoftheday.uaa.bootstrap;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import net.codeoftheday.uaa.domain.dto.ClientDto;
import net.codeoftheday.uaa.domain.dto.UserDto;

@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Bootstrap {

	@Getter
	@Setter
	private List<UserDto> users = new ArrayList<>();

	@Getter
	@Setter
	private List<ClientDto> clients = new ArrayList<>();

}
