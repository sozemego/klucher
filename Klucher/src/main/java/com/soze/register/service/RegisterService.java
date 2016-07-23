package com.soze.register.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.soze.register.model.RegisterForm;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class RegisterService {

  private static final int MAX_USERNAME_LENGTH = 32;
  private static final int MIN_PASSWORD_LENGTH = 6;
  private static final int MAX_PASSWORD_LENGTH = 64;
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
   * 
   * @param form
   * @throws IllegalArgumentException if username is null, too long or empty, or if password is null, empty
   *    or too short/long or if username is not available
   */
  public void register(RegisterForm form) throws IllegalArgumentException {
    log.info("Attempting to register user with username [{}].", form.getUsername());
    validateInput(form);
    if (!isAvailable(form.getUsername())) {
      throw new IllegalArgumentException("User with given username already exists.");
    }
    User user = registerConverter.convertRegisterForm(form);
    userDao.save(user);
  }

  private void validateInput(RegisterForm form) throws IllegalArgumentException {
    validateUsername(form.getUsername());
    validatePassword(form.getPassword());
  }

  private void validateUsername(String username) throws IllegalArgumentException {
    if (!StringUtils.hasText(username)) {
      throw new IllegalArgumentException(
          "Username cannot be null, empty or consist of only whitespace.");
    }
    if (username.length() > MAX_USERNAME_LENGTH) {
      throw new IllegalArgumentException(
          "Username should not be longer than " + MAX_USERNAME_LENGTH + " characters.");
    }
  }

  private void validatePassword(String password) throws IllegalArgumentException {
    if (!StringUtils.hasText(password)) {
      throw new IllegalArgumentException(
          "Password cannot be null, empty or consist of only whitespace.");
    }
    if (password.length() > MAX_PASSWORD_LENGTH) {
      throw new IllegalArgumentException(
          "Password should not be longer than " + MAX_PASSWORD_LENGTH + " characters.");
    }
    if(password.length() < MIN_PASSWORD_LENGTH) {
      throw new IllegalArgumentException(
          "Password should not be shorter than " + MIN_PASSWORD_LENGTH + " characters.");
    }
  }

  public boolean isAvailable(String username) {
    return !userDao.exists(username);
  }

}
