package com.soze.register.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.soze.register.model.RegisterForm;
import com.soze.user.model.User;
import com.soze.user.model.UserRoles;
import com.soze.utils.FileUtils;

/**
 * Converts registration forms into {@link User} objects.
 * 
 * @author sozek
 *
 */
@Service
public class RegisterConverter {

	private static final Logger log = LoggerFactory.getLogger(RegisterConverter.class);
	private static final String AVATARS_PATH = "config/avatars.txt";
	private final List<String> avatarPaths = new ArrayList<>();
	private final Random random = new Random();
  private final PasswordEncoder passwordEncoder;
  private final FileUtils fileUtils;
  
  @Autowired
  public RegisterConverter(PasswordEncoder passwordEncoder, FileUtils fileUtils) {
    this.passwordEncoder = passwordEncoder;
    this.fileUtils = fileUtils;
  }
  
  @PostConstruct
  public void init() {
  	try {
  		List<String> loadedAvatarPaths = fileUtils.readLinesFromClasspathFile(AVATARS_PATH);
  		log.info("Loaded [{}] avatar paths. ", loadedAvatarPaths.size());
  		avatarPaths.addAll(loadedAvatarPaths);
  	} catch (IOException e) {
  		System.out.println(e);
  	}
  }

  /**
   * Accepts a {@link RegisterForm} and converts it to a {@link User}
   * object which is ready to be persisted to a database.
   * This method does NO validation. Sets user roles to contain only "ROLE_USER".
   * @param form
   * @return
   */
  public User convertRegisterForm(RegisterForm form) { 
    String hashedPassword = passwordEncoder.encode(form.getPassword());
    UserRoles userRoles = new UserRoles();
    userRoles.setUser(true);
    userRoles.setAdmin(false);
    User user = new User(form.getUsername(), hashedPassword, userRoles);
    user.setAvatarPath(getRandomAvatarPath());
    return user;
  }
  
  private String getRandomAvatarPath() {
  	int randomIndex = random.nextInt(avatarPaths.size());
  	return avatarPaths.get(randomIndex);
  }

}
