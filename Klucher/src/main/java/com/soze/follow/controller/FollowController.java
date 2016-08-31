package com.soze.follow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NotLoggedInException;
import com.soze.follow.service.FollowService;

@Controller
public class FollowController {
  
  private static final Logger log = LoggerFactory.getLogger(FollowController.class);
  private final FollowService followService;

  @Autowired
  public FollowController(FollowService followService) {
    this.followService = followService;
  }
  
  @RequestMapping(value = "/user/follow", method = RequestMethod.POST)
  public ResponseEntity<String> follow(Authentication authentication, @RequestParam String follow) throws Exception {  
  	
    validate(authentication, follow);
    String username = authentication.getName();
    log.info("User [{}] tried to follow [{}]", username, follow);
    followService.follow(username, follow);
    return new ResponseEntity<String>(HttpStatus.OK);
    
  }
  
  @RequestMapping(value = "/user/unfollow", method = RequestMethod.POST)
  public ResponseEntity<String> unfollow(Authentication authentication, @RequestParam String follow) throws Exception {
  	
    validate(authentication, follow);
    String username = authentication.getName();
    log.info("User [{}] tried to unfollow [{}]", username, follow);
    followService.unfollow(username, follow);
    return new ResponseEntity<String>(HttpStatus.OK);
    
  }

  /**
   * Checks if the user is logged in and checks if the user and the user they are trying to follow/unfollow
   * are the same.
   * @param authentication
   * @param follow name of the user to follow/unfollow
   * @throws NotLoggedInException if the user is not logged in
   * @throws CannotDoItToYourselfException if the user is trying to follow/unfollow himself
   */
  private void validate(Authentication authentication, String follow)
      throws NotLoggedInException, CannotDoItToYourselfException {
    if(authentication == null) {
      throw new NotLoggedInException();
    }
    String username = authentication.getName();
    if(username.equals(follow)) {
      throw new CannotDoItToYourselfException(username, "follow/unfollow");
    }
  }
  
}
