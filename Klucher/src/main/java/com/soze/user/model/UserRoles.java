package com.soze.user.model;

import java.io.Serializable;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

@Embeddable
public class UserRoles implements Serializable {

	private static final long serialVersionUID = -5506739313096234155L;
	
	@NotNull
	private boolean user;
	@NotNull
	private boolean admin;

	public UserRoles() {

	}

	public UserRoles(boolean user, boolean admin) {
		this.user = user;
		this.admin = admin;
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
