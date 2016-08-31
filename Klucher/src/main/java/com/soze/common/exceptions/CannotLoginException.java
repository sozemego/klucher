package com.soze.common.exceptions;

/**
 * Requires to use either of parent's constuctors (for logging),
 * but this information should not be exposed to the user.
 * @author sozek
 *
 */
@SuppressWarnings("serial")
public class CannotLoginException extends RuntimeException {

	public CannotLoginException(String message) {
		super(message);
	}
	
	public CannotLoginException(String message, Throwable cause) {
		super(message, cause);
	}
	
}
