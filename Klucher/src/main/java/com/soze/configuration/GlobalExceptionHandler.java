package com.soze.configuration;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.soze.common.errorresponse.ErrorResponse;
import com.soze.common.exceptions.HttpException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler
  public ResponseEntity<Object> handleIllegal(IllegalArgumentException ex) {
    log.info("IllegalArgumentException", ex);
    return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler
  public ResponseEntity<Object> handleHttpException(HttpException ex) {
    log.info("HttpException", ex);
    return ex.getResponseEntity();
  }

  @Override
  public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
      HttpHeaders headers, HttpStatus status, WebRequest request) {
    List<String> errors = new ArrayList<>();
    for (FieldError error : ex.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }
    for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
      errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
    }
    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),
        errors);
    return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    String error = ex.getParameterName() + " parameter is missing";
    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),
        error);
    return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
  }

  @Override
  public ResponseEntity<Object> handleMissingServletRequestPart(
      MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status,
      WebRequest request) {
    String error = "parameter " + ex.getRequestPartName() + " is missing";
    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(),
        error);
    return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
  }
  
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
    String error = ex.getName() + " is required to be of type " + ex.getRequiredType().getName();
    ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
    return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
  }
  
  @Override
  protected ResponseEntity<Object> handleNoHandlerFoundException(
    NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
      String error = "No handler found for " + ex.getHttpMethod() + " at " + ex.getRequestURL();
      ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
      return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
  }
  
  @Override
  protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
    HttpRequestMethodNotSupportedException ex, 
    HttpHeaders headers, 
    HttpStatus status, 
    WebRequest request) {
      StringBuilder builder = new StringBuilder();
      builder.append(ex.getMethod() + " method is not supported for this request. Supported methods are ");
      ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));
   
      ErrorResponse response = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, 
        ex.getLocalizedMessage(), builder.toString());
      return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
  }
  
  @ExceptionHandler(Exception.class)
  public ResponseEntity<Object> handleFallBack() {
    return new ResponseEntity<Object>("error", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

}
