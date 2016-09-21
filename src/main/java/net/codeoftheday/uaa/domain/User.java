package net.codeoftheday.uaa.domain;

import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Email;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user")
@ToString(exclude = { "password" })
public class User implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@Column(columnDefinition = "char(36)")
	@Getter
	@Setter
	private String id;

	@NotNull
	@Pattern(regexp = "^[a-z0-9]*$")
	@Size(min = 1, max = 50)
	@Column(length = 50, unique = true, nullable = false)
	@Getter
	@Setter
	private String username;

	@NotNull
	@Email
	@Size(max = 100)
	@Column(length = 100, unique = true)
	@Getter
	@Setter
	private String email;

	@JsonIgnore
	@NotNull
	@Size(min = 60, max = 60)
	@Column(name = "password_hash", length = 60)
	@Getter
	@Setter
	private String password;

	@JsonIgnore
	@ManyToMany(fetch = EAGER)
	@JoinTable(name = "user_authority", joinColumns = {
			@JoinColumn(name = "user_id", referencedColumnName = "id") }, inverseJoinColumns = {
					@JoinColumn(name = "authority_id", referencedColumnName = "id") })
	@Getter
	@Setter
	private Set<Authority> authorities = new HashSet<>();

	@Getter
	@Setter
	@Size(min = 12, max = 100)
	private String activationCode;

	@Getter
	@Setter
	private String locale;

	@Getter
	@Setter
	@Size(min = 12, max = 100)
	private String passwordResetCode;

	@Getter
	@Setter
	private Instant passwordResetTimestamp;

	public User() {
		// JPA
	}

	@SuppressWarnings("squid:UnusedProtectedMethod")
	@PrePersist
	protected void prePersist() {
		if (this.id == null) {
			this.id = UUID.randomUUID().toString();
		}
	}

	public void addAuthority(final Authority authority) {
		getAuthorities().add(authority);
	}
}
