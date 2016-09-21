package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class InvalidOwnerException extends RuntimeException {

	// owner of what?
	private final String property;

	public InvalidOwnerException(String property) {
		this.property = property;
	}

	public String getProperty() {
		return property;
	}

}
