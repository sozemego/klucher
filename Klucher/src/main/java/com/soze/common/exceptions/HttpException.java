package com.soze.common.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.soze.common.errorresponse.ErrorResponse;

/**
 * Custom exception which can construct a {@link ResponseEntity} object for
 * convenience. It's named HttpException because it has a {@link HttpStatus}
 * as a field.
 * @author sozek
 *
 */
@SuppressWarnings("serial")
public class HttpException extends Exception {

  private final HttpStatus status;

  public HttpException(String message, HttpStatus status) {
    this(message, null, status);
  }

  public HttpException(String message, Throwable cause, HttpStatus status) {
    super(message, cause);
    this.status = status;
  }

  public ResponseEntity<Object> getResponseEntity() {
    ErrorResponse response = new ErrorResponse(status, this.getMessage());
    return new ResponseEntity<Object>(response, new HttpHeaders(), status);
  }

}
