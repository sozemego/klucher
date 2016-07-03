package com.soze.kluch.exceptions;

/**
 * Exception throw if the content of a Kluch is too long or otherwise illegal.
 * @author sozek
 *
 */
public class InvalidKluchContentException extends KluchException {

  public InvalidKluchContentException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidKluchContentException(String message) {
    super(message);
  }

  public InvalidKluchContentException(Throwable cause) {
    super(cause);
  }

}
