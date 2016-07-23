package com.soze.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.soze.common.exceptions.HttpException;

@ControllerAdvice
public class GlobalExceptionHandler {

  private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  
  @ExceptionHandler
  public ResponseEntity<String> handleIllegal(IllegalArgumentException ex) {
    log.info("IllegalArgumentException", ex);
    return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
  
  @ExceptionHandler
  public ResponseEntity<String> handleHttpException(HttpException ex) {
    log.info("HttpException", ex);
    return ex.getResponseEntity();
  }
  
  @ExceptionHandler
  public ResponseEntity<String> handleMissingParameter(MissingServletRequestParameterException ex) {
    log.info("MissingServletRequestParameterException", ex);
    return new ResponseEntity<String>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
  
}
