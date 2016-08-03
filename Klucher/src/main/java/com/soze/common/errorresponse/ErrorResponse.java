package com.soze.common.errorresponse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ErrorResponse {

  private final HttpStatus status;
  private final String message;
  private final List<String> errors;
  
  public ErrorResponse(HttpStatus status, String message, List<String> errors) {
    this.status = status;
    this.message = message;
    this.errors = errors;
  }
  
  public ErrorResponse(HttpStatus status, String message, String error) {
    this.status = status;
    this.message = message;
    this.errors = Arrays.asList(error);
  }
  
  public ErrorResponse(HttpStatus status, String message) {
    this.status = status;
    this.message = message;
    this.errors = new ArrayList<>(0);
  }
  
  public HttpStatus getStatus() {
    return status;
  }
  
  public String getMessage() {
    return message;
  }
  
  public List<String> getErrors() {
    return errors;
  }
  
  
}
