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
import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.CannotLoginException;
import com.soze.common.exceptions.ChatRoomDoesNotExistException;
import com.soze.common.exceptions.ContainsWhiteSpaceException;
import com.soze.common.exceptions.InvalidLengthException;
import com.soze.common.exceptions.InvalidOwnerException;
import com.soze.common.exceptions.InvalidTimestampException;
import com.soze.common.exceptions.InvalidUserSettingException;
import com.soze.common.exceptions.KluchDoesNotExistException;
import com.soze.common.exceptions.KluchPreviouslyPostedException;
import com.soze.common.exceptions.NotLoggedInException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.TooManyRequestsException;
import com.soze.common.exceptions.UserAlreadyExistsException;
import com.soze.common.exceptions.UserDoesNotExistException;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(NotLoggedInException.class)
	public ResponseEntity<Object> handleNotLoggedInException(NotLoggedInException e) {
		String message = "You need to be logged in to do this.";
		return getResponse(message, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(CannotDoItToYourselfException.class)
	public ResponseEntity<Object> handleCannotDoItToYourselfException(CannotDoItToYourselfException e) {
		log.info("User [{}] tried to do [{}] to himself. ", e.getUsername(), e.getAction());
		String message = "You cannot do that to yourself.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CannotLoginException.class)
	public ResponseEntity<Object> handleCannotLoginException(CannotLoginException e) {
		log.info("", e);
		String message = "Wrong username or password.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(TooManyRequestsException.class)
	public ResponseEntity<Object> handleTooManyRequestsException(TooManyRequestsException e) {
		log.info(e.getResult().toString());
		String message = "You have made too many requests.";
		return getResponse(message, HttpStatus.TOO_MANY_REQUESTS);
	}

	@ExceptionHandler(InvalidTimestampException.class)
	public ResponseEntity<Object> handleInvalidTimestampException(InvalidTimestampException e) {
		String message = "Timestamp cannot be negative.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(NullOrEmptyException.class)
	public ResponseEntity<Object> handleNullOrEmptyException(NullOrEmptyException e) {
		String message = e.getParameter() + " cannot be empty.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserDoesNotExistException.class)
	public ResponseEntity<Object> handleUserDoesNotExistException(UserDoesNotExistException e) {
		String message = "User " + e.getUsername() + " does not exist.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(KluchPreviouslyPostedException.class)
	public ResponseEntity<Object> handleKluchPreviouslyPostedException(KluchPreviouslyPostedException e) {
		log.info("User [{}] previously posted an identical kluch.", e.getUsername());
		String message = "Your last Kluch was identical to this one.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ContainsWhiteSpaceException.class)
	public ResponseEntity<Object> handleContainsWhiteSpaceException(ContainsWhiteSpaceException e) {
		String message = e.getParameter() + " cannot contain spaces.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidLengthException.class)
	public ResponseEntity<Object> handleInvalidLengthException(InvalidLengthException e) {
		String message = e.getParameter() + " was too " + e.getAdjective() + ".";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<Object> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
		log.info("Someone tried to register [{}] but it already exists.", e.getUsername());
		String message = "This username is not available.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Object> handleIllegal(IllegalArgumentException ex) {
		log.info("IllegalArgumentException", ex);
		return getResponse("Some error occured", HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(InvalidOwnerException.class)
	public ResponseEntity<Object> handleInvalidOwnerException(InvalidOwnerException e) {
		String message = "You are not the owner of " + e.getProperty();
		return getResponse(message, HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(KluchDoesNotExistException.class)
	public ResponseEntity<Object> handleKluchDoesNotExistException(KluchDoesNotExistException e) {
		String message = "Kluch does not exist";
		return getResponse(message, HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(InvalidUserSettingException.class)
	public ResponseEntity<Object> handleInvalidUserSettingException(InvalidUserSettingException ex) {
		String message = ex.getInvalidSetting() + " setting was invalid.";
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(ChatRoomDoesNotExistException.class)
	public ResponseEntity<Object> handleChatRoomDoesNotExistException(ChatRoomDoesNotExistException ex) {
		log.info("Someone tried to join room " + ex.getRoomName());
		return getResponse("Cannot join room", HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex,
			WebRequest request) {
		String message = ex.getName() + " is required to be of type " + ex.getRequiredType().getSimpleName();
		return getResponse(message, HttpStatus.BAD_REQUEST);
	}

	@Override
	public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		List<String> errors = new ArrayList<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			errors.add(error.getField() + ": " + error.getDefaultMessage());
		}
		for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
			errors.add(error.getObjectName() + ": " + error.getDefaultMessage());
		}
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), errors);
		return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
	}

	@Override
	protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = ex.getParameterName() + " parameter is missing";
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
	}

	@Override
	public ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		String error = "parameter " + ex.getRequestPartName() + " is missing";
		ErrorResponse response = new ErrorResponse(HttpStatus.BAD_REQUEST, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
	}

	@Override
	protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		String error = "No handler found for " + ex.getHttpMethod() + " at " + ex.getRequestURL();
		ErrorResponse response = new ErrorResponse(HttpStatus.NOT_FOUND, ex.getLocalizedMessage(), error);
		return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
	}

	/**
	@Override
	protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		StringBuilder builder = new StringBuilder();
		builder.append(ex.getMethod() + " method is not supported for this request. Supported methods are ");
		ex.getSupportedHttpMethods().forEach(t -> builder.append(t + " "));

		ErrorResponse response = new ErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, ex.getLocalizedMessage(),
				builder.toString());
		return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
	}
*/
	
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> handleFallBack(Exception e) {
		log.info("Some exception was thrown.", e);
		return new ResponseEntity<Object>("error", new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
	}

	private ResponseEntity<Object> getResponse(String message, HttpStatus status) {
		ErrorResponse response = new ErrorResponse(status, message);
		return new ResponseEntity<Object>(response, new HttpHeaders(), response.getStatus());
	}

}
