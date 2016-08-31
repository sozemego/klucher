package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class CannotDoItToYourselfException extends RuntimeException {

	private final String username;
	private final String action;

	public CannotDoItToYourselfException(String username, String action) {
		this.username = username;
		this.action = action;
	}

	public String getUsername() {
		return username;
	}

	public String getAction() {
		return action;
	}

}
