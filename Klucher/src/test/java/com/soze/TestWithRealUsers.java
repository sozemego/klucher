package com.soze;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.soze.register.model.RegisterForm;
import com.soze.register.service.RegisterConverter;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * Tests which require users be present in a database/be logged in
 * should extend this class and use methods contained therein.
 * @author sozek
 *
 */
public class TestWithRealUsers {

  @Autowired
  private UserDao userDao;
  
  @Autowired
  private RegisterConverter registerConverter;
  
  protected void addUser(String username, String password) {
    addUser(username, password, false);
  }
  
  protected void addUser(String username, String password, boolean login) {
    User user = getBaseUser(username, password);
    userDao.save(user);
    if(login) {
      SecurityContextHolder.getContext().setAuthentication(
          new UsernamePasswordAuthenticationToken(username,
              user.getHashedPassword(), user.getAuthorities()));
    }
  }

  private User getBaseUser(String username, String password) {
    RegisterForm form = new RegisterForm();
    form.setUsername(username);
    form.setPassword(password);
    User testUser = registerConverter.convertRegisterForm(form);
    return testUser;
  }
}
