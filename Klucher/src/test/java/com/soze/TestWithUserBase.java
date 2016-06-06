package com.soze;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import com.soze.register.model.RegisterForm;
import com.soze.register.service.RegisterConverter;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.model.UserRoles;

/**
 * Tests which require users be present in a database/be logged in
 * should extend this class and use methods contained therein.
 * @author sozek
 *
 */
public class TestWithUserBase {

  @Autowired
  private UserDao userDao;
  
  @Autowired
  private RegisterConverter registerConverter;
  
  protected void addUserToDb(String username, String password) {
    addUserToDb(username, password, false);
  }
  
  protected void addUserToDb(String username, String password, boolean admin) {
    User user = getBaseUser(username, password);
    if(admin) {
      UserRoles roles = new UserRoles(username, true, true);
      user.setUserRoles(roles);
    }
    userDao.save(user);
  }
  
  protected void addUserToDbAndLogin(String username, String password) {
    addUserToDbAndLogin(username, password, false);
  }
  
  protected void addUserToDbAndLogin(String username, String password, boolean admin) {
    addUserToDb(username, password, admin);
    loginUser(username);
  }
  
  
  private void loginUser(String username) {
    User user = userDao.findOne(username);
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(username,
            user.getHashedPassword()));
  }

  private User getBaseUser(String username, String password) {
    RegisterForm form = new RegisterForm();
    form.setUsername(username);
    form.setPassword(password);
    User testUser = registerConverter.convertRegisterForm(form);
    return testUser;
  }
}
