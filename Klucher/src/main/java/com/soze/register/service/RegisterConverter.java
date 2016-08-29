package com.soze.register.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.soze.register.model.RegisterForm;
import com.soze.user.model.User;
import com.soze.user.model.UserRoles;

/**
 * Converts registration forms into {@link User} objects.
 * 
 * @author sozek
 *
 */
@Service
public class RegisterConverter {

  private final PasswordEncoder passwordEncoder;
  
  @Autowired
  public RegisterConverter(PasswordEncoder passwordEncoder) {
    this.passwordEncoder = passwordEncoder;
  }

  /**
   * Accepts a {@link RegisterForm} and converts it to a {@link User}
   * object which is ready to be persisted to a database.
   * This method does NO validation. Sets user roles to contain only "ROLE_USER".
   * @param form
   * @return
   */
  public User convertRegisterForm(RegisterForm form) {
    User user = new User();
    user.setUsername(form.getUsername());
    String hashedPassword = passwordEncoder.encode(form.getPassword());
    user.setHashedPassword(hashedPassword);
    UserRoles userRoles = new UserRoles();
    userRoles.setUsername(form.getUsername());
    userRoles.setUser(true);
    userRoles.setAdmin(false);
    user.setUserRoles(userRoles);
    return user;
  }

}
