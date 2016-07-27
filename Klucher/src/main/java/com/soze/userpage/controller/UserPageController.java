package com.soze.userpage.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Controller
public class UserPageController {
  
  private final UserDao userDao;
  
  @Autowired
  public UserPageController(UserDao userDao) {
    this.userDao = userDao;
  }
  
  @RequestMapping(value = "/u/{username}", method = RequestMethod.GET)
  public String userPage(Authentication authentication, @PathVariable String username, Model model) {    
    boolean loggedIn = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    if (loggedIn) {
      @SuppressWarnings("null")
      String authorizedUsername = authentication.getName();
      if(authorizedUsername.equals(username)) {
        return "redirect:/dashboard";
      }
    }
    User user = userDao.findOne(username);
    if(user == null) {
      return "front";
    }
    if(loggedIn) {
      String authorizedUsername = authentication.getName();
      model.addAttribute("follows", doesUsernameFollow(authorizedUsername, user));
    }
    model.addAttribute("username", username);
    model.addAttribute("loggedIn", authentication != null);
    
    return "user";
  }
  
  private boolean doesUsernameFollow(String username, User follow) {
    Set<String> followers = follow.getFollowers();
    if(followers.contains(username)) {
      return true;
    }
    return false;
  }
  
  
}
