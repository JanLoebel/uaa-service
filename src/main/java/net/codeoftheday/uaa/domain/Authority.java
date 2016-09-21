package net.codeoftheday.uaa.domain;

import java.io.Serializable;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "authority")
@ToString
public class Authority implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@NotNull
	@Column(columnDefinition = "char(36)")
	@Getter
	@Setter
	private String id;

	@Size(min = 1, max = 50)
	@Column(length = 50)
	@Getter
	@Setter
	private String name;

	public Authority() {
		// JPA
	}

	public Authority(final String name) {
		this.name = name;
	}

	@SuppressWarnings("squid:UnusedProtectedMethod")
	@PrePersist
	protected void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
	}
}
