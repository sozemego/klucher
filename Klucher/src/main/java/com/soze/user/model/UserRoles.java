package com.soze.user.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class UserRoles implements Serializable {

	private static final long serialVersionUID = -5506739313096234155L;
	
	@Id
	@GeneratedValue
	private Long id;
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
