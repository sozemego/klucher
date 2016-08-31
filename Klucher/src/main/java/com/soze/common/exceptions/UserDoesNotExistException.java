package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class UserDoesNotExistException extends RuntimeException {

	private final String username;
	
	public UserDoesNotExistException(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
