package com.soze.kluch.exceptions;

/**
 * Abstract class for exceptions thrown by object which take care of Kluchs,
 * for instance {@link KluchService}.
 * @author sozek
 *
 */
public abstract class KluchException extends Exception {

  public KluchException(String message, Throwable cause) {
    super(message, cause);
  }
  
  public KluchException(String message) {
    super(message);
  }
  
  public KluchException(Throwable cause) {
    super(cause);
  }
  
}
