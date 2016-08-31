package com.soze.register.service;

import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.ContainsWhiteSpaceException;
import com.soze.common.exceptions.InvalidLengthException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserAlreadyExistsException;
import com.soze.common.exceptions.InvalidLengthException.Adjective;
import com.soze.register.model.RegisterForm;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class RegisterService {
  
  private static final int MAX_USERNAME_LENGTH = 32;
  private static final int MIN_PASSWORD_LENGTH = 6;
  private static final int MAX_PASSWORD_LENGTH = 64;
  private static final Pattern WHITE_SPACE = Pattern.compile("\\s");
  private static final Logger log = LoggerFactory.getLogger(RegisterService.class);

  private final UserDao userDao;
  private final RegisterConverter registerConverter;

  @Autowired
  public RegisterService(UserDao userDao, RegisterConverter registerConverter) {
    this.userDao = userDao;
    this.registerConverter = registerConverter;
  }

  /**
   * Attempts to register user using given register form.
   * @param form
   * @throws UserAlreadyExistsException if user already exists
   * @throws NullOrEmptyException if username or password are null or empty
   * @throws InvalidLengthException if username or password have invalid lengths
   * @throws ContainsWhiteSpaceException if user or password contain white spaces
   */
  public User register(RegisterForm form)
  		throws UserAlreadyExistsException, NullOrEmptyException, InvalidLengthException, ContainsWhiteSpaceException {
    log.info("Attempting to register user with username [{}].", form.getUsername());
    validateInput(form);
    if (!isAvailable(form.getUsername())) {
      throw new UserAlreadyExistsException(form.getUsername());
    }
    User user = registerConverter.convertRegisterForm(form);
    return userDao.save(user);
  }

  private void validateInput(RegisterForm form) throws NullOrEmptyException, InvalidLengthException, ContainsWhiteSpaceException {
    validateUsername(form.getUsername());
    validatePassword(form.getPassword());
  }

	private void validateUsername(String username)
			throws NullOrEmptyException, InvalidLengthException, ContainsWhiteSpaceException {
		if (username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
		if (username.length() > MAX_USERNAME_LENGTH) {
			throw new InvalidLengthException("Username", Adjective.LONG);
		}
		if (hasWhiteSpace(username)) {
			throw new ContainsWhiteSpaceException("Username");
		}
	}

	private void validatePassword(String password)
			throws NullOrEmptyException, InvalidLengthException, ContainsWhiteSpaceException {
		if (password.length() > MAX_PASSWORD_LENGTH) {
			throw new InvalidLengthException("Password", Adjective.LONG);
		}
		if (password.length() < MIN_PASSWORD_LENGTH) {
			throw new InvalidLengthException("password", Adjective.SHORT);
		}
		if (hasWhiteSpace(password)) {
			throw new ContainsWhiteSpaceException("Password");
		}
	}

  public boolean isAvailable(String username) {
    return !userDao.exists(username);
  }
  
  private boolean hasWhiteSpace(String text) {
    return WHITE_SPACE.matcher(text).find();
  }

}
