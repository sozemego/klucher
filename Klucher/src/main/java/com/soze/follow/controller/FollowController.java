package com.soze.follow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.follow.service.FollowService;
import com.soze.notification.service.NotificationService;

@Controller
public class FollowController {
  
  private static final Logger log = LoggerFactory.getLogger(FollowController.class);
  private final FollowService followService;
  private final NotificationService notificationService;

  @Autowired
  public FollowController(
  		FollowService followService,
  		@Qualifier("NotificationServiceWithCache") NotificationService notificationService) {
    this.followService = followService;
    this.notificationService = notificationService;
  }
  
  @RequestMapping(value = "/user/follow", method = RequestMethod.POST)
  public ResponseEntity<String> follow(Authentication authentication, @RequestParam String follow) throws Exception {  
  	if(authentication == null) {
  		throw new NotLoggedInException();
  	}
    String username = authentication.getName();
    log.info("User [{}] tried to follow [{}]", username, follow);
    followService.follow(username, follow);
    notificationService.addFollowNotification(username, follow);
    return new ResponseEntity<String>(HttpStatus.OK);
    
  }
  
  @RequestMapping(value = "/user/unfollow", method = RequestMethod.POST)
  public ResponseEntity<String> unfollow(Authentication authentication, @RequestParam String follow) throws Exception {
  	if(authentication == null) {
  		throw new NotLoggedInException();
  	}
    String username = authentication.getName();
    log.info("User [{}] tried to unfollow [{}]", username, follow);
    followService.unfollow(username, follow);
    notificationService.removeFollowNotification(username, follow);
    return new ResponseEntity<String>(HttpStatus.OK);
    
  }
}
