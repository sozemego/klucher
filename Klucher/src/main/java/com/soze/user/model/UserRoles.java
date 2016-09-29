package com.soze.user.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class UserRoles {

	@Id
	@GeneratedValue
	private Long id;
	@NotNull
	private boolean user;
	@NotNull
	private boolean admin;

	public UserRoles() {

	}

	public UserRoles(Long id, boolean user, boolean admin) {
		this.user = user;
		this.admin = admin;
		this.id = id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public boolean isUser() {
		return user;
	}

	public void setUser(boolean user) {
		this.user = user;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

}
