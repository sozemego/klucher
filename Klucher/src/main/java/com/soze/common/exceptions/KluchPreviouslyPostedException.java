package com.soze.common.exceptions;

@SuppressWarnings("serial")
public class KluchPreviouslyPostedException extends RuntimeException {

	private final String username;
	
	public KluchPreviouslyPostedException(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}
	
}
