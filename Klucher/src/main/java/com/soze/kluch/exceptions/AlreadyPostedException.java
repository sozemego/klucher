package com.soze.kluch.exceptions;

/**
 * Exception throw when a Kluch was previously posted and the same user
 * attempts to post it again.
 * @author sozek
 *
 */
public class AlreadyPostedException extends KluchException {

  public AlreadyPostedException(String message, Throwable cause) {
    super(message, cause);
  }

  public AlreadyPostedException(String message) {
    super(message);
  }

  public AlreadyPostedException(Throwable cause) {
    super(cause);
  }

}
