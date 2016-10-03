package com.soze.user.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import com.soze.register.model.RegisterForm;
import com.soze.register.service.RegisterConverter;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.model.UserRoles;
import com.soze.utils.FileUtils;

@Service
@Profile("dev")
public class UserPopulation {

  private static final Logger log = LoggerFactory
      .getLogger(UserPopulation.class);
  private static final String DEV_USERS_PATH = "dev/devUsers.txt";
  private static final String DEFAULT_PASSWORD = "password";
  private final UserDao userDao;

  private final RegisterConverter registerConverter;

  private final FileUtils fileUtils;

  @Autowired
  public UserPopulation(UserDao userDao, RegisterConverter registerConverter,
      FileUtils fileUtils) {
    this.userDao = userDao;
    this.registerConverter = registerConverter;
    this.fileUtils = fileUtils;
  }

  @PostConstruct
  public void init() {
    log.info("Prepopulating database with users.");
    List<User> users = constructTestUsers();
    for (User user : users) {
    	if(userDao.findOne(user.getUsername()) == null) {
    		userDao.save(user);
    	}
    }
    log.info("Added [{}] users", users.size());
  }

  private List<User> constructTestUsers() {
    List<User> users = new ArrayList<>();
    try {
      List<String> userLines = fileUtils.readLinesFromFile(DEV_USERS_PATH);
      for (String user : userLines) {
        users.add(parseUser(user));
      }
    } catch (IOException e) {
      log.info("Couldn't load test users.", e);
    }
    return users;
  }

  private User parseUser(String userString) {
    String[] tokens = userString.split(",");
    RegisterForm form = new RegisterForm();
    form.setUsername(tokens[0]);
    form.setPassword(tokens.length > 1 ? tokens[1] : DEFAULT_PASSWORD);
    User user = registerConverter.convertRegisterForm(form);
    UserRoles userRoles = new UserRoles();
    //userRoles.setUsername(tokens[0]);
    userRoles.setUser(tokens.length > 2 ? (tokens[2].equals("1") ? true : false) : true);
    userRoles.setAdmin(tokens.length > 3 ? (tokens[3].equals("1") ? true : false) : false);
    user.setUserRoles(userRoles);
    return user;
  }

}
