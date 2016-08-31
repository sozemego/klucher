package com.soze.common.exceptions;

/**
 * Exception used in cases where user is not logged in,
 * but an action requires authentication.
 * @author sozek
 *
 */
@SuppressWarnings("serial")
public class NotLoggedInException extends RuntimeException {
	
}
