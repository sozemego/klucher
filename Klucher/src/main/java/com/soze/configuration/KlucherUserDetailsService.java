package com.soze.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class KlucherUserDetailsService implements UserDetailsService {

  private final UserDao userDao;

  @Autowired
  public KlucherUserDetailsService(UserDao userDao) {
    this.userDao = userDao;
  }

  @Override
  public User loadUserByUsername(String username)
      throws UsernameNotFoundException {
    User user = userDao.findOne(username);
    if (user == null) {
      throw new UsernameNotFoundException(
          "Account: " + username + " doesn't exist.");
    }
    return user;
  }

}
