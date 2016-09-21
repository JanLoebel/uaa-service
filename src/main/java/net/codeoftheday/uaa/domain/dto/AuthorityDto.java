package net.codeoftheday.uaa.domain.dto;

import javax.persistence.Column;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;

import lombok.Getter;
import lombok.ToString;
import net.codeoftheday.uaa.domain.Authority;

@ToString
public class AuthorityDto {

	@Getter
	@JsonProperty(access = Access.READ_ONLY)
	private String id;

	@Size(min = 1, max = 50)
	@Column(length = 50)
	@Getter
	private String name;

	public AuthorityDto() {
		// JACKSON
	}

	public AuthorityDto(final Authority authority) {
		this.id = authority.getId();
		this.name = authority.getName();
	}

	public AuthorityDto(final String name) {
		this.name = name;
	}
}
