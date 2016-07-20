package com.soze.follow.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.user.dao.UserDao;

@Controller
public class FollowController {
  
  private final UserDao userDao;

  @Autowired
  public FollowController(UserDao userDao) {
    this.userDao = userDao;
  }
  
  @RequestMapping(value = "/user/follow", method = RequestMethod.POST)
  public ResponseEntity<String> follow(Authentication authentication, @RequestParam String follow) {
    if(authentication == null) {
      return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    }
    if(follow == null) {
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    String username = authentication.getName();
    userDao.follow(username, follow);
    return new ResponseEntity<String>(HttpStatus.OK);
  }
  
  @RequestMapping(value = "/user/unfollow", method = RequestMethod.POST)
  public ResponseEntity<String> unfollow(Authentication authentication, @RequestParam String follow) {
    if(authentication == null) {
      return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    }
    if(follow == null) {
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    }
    String username = authentication.getName();
    userDao.unfollow(username, follow);
    return new ResponseEntity<String>(HttpStatus.OK);
  }
  
}
