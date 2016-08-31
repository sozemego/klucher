package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class UserAlreadyExistsException extends RuntimeException {

	private final String username;
	
	public UserAlreadyExistsException(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
