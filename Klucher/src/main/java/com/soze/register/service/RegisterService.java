package com.soze.register.service;

import java.util.Map;

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

  private static final int MIN_USERNAME_LENGTH = 4;
  private static final int MAX_USERNAME_LENGTH = 64;
  private static final int MIN_PASSWORD_LENGTH = 6;
  private static final int MAX_PASSWORD_LENGTH = 64;
  private static final Logger log = LoggerFactory
      .getLogger(RegisterService.class);

  private final UserDao userDao;
  private final RegisterConverter registerConverter;

  @Autowired
  public RegisterService(UserDao userDao, RegisterConverter registerConverter) {
    this.userDao = userDao;
    this.registerConverter = registerConverter;
  }

  /**
   * Attempts to register a new {@liln User}. Any validation errors are stored
   * in the errors map.
   * 
   * @param form
   * @param errors
   *          validation errors are stored here
   */
  public void register(RegisterForm form, Map<String, String> errors) {
    log.info("Attempting to register user with username [{}].",
        form.getUsername());
    errorMessages(form, errors);
    if (!errors.isEmpty()) {
      return;
    }
    boolean exists = userDao.exists(form.getUsername());
    if (exists) {
      errors.put("general", "User with given name already exists.");
      return;
    }
    User user = registerConverter.convertRegisterForm(form);
    userDao.save(user);
  }

  private Map<String, String> errorMessages(RegisterForm form,
      Map<String, String> errors) {
    String username = form.getUsername();
    if (username == null || !StringUtils.hasText(username)) {
      errors.put("username_error", "Username should be at least "
          + MIN_USERNAME_LENGTH + " characters long.");
    } else {
      if (!username.isEmpty() && username.length() < MIN_USERNAME_LENGTH) {
        errors.put("username_error", "Username should be at least "
            + MIN_USERNAME_LENGTH + " characters long.");
      }
      if (username.length() > MAX_USERNAME_LENGTH) {
        errors.put("username_error", "Username should not be longer than "
            + MAX_USERNAME_LENGTH + " characters.");
      }
    }
    String password = form.getPassword();
    if (password == null || !StringUtils.hasText(password)) {
      errors.put("password_error", "Password should be at least "
          + MIN_PASSWORD_LENGTH + " characters long.");
    } else {
      if (!password.isEmpty() && password.length() < MIN_PASSWORD_LENGTH) {
        errors.put("password_error", "Password should be at least "
            + MIN_PASSWORD_LENGTH + " characters long.");
      }
      if (password.length() > MAX_PASSWORD_LENGTH) {
        errors.put("password_error", "Password should not be longer than "
            + MAX_PASSWORD_LENGTH + " characters.");
      }
    }
    return errors;
  }
  
  public boolean isAvailable(String username) {
    return !userDao.exists(username);
  }

}
