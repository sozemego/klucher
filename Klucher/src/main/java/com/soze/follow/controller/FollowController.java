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

import com.soze.common.exceptions.HttpException;
import com.soze.follow.service.FollowService;
import com.soze.ratelimiter.service.RateLimiter;

@Controller
public class FollowController {
  
  private static final Logger log = LoggerFactory.getLogger(FollowController.class);
  private final FollowService followService;
  private final RateLimiter rateLimiter;

  @Autowired
  public FollowController(FollowService followService, RateLimiter rateLimiter) {
    this.followService = followService;
    this.rateLimiter = rateLimiter;
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

  private void validate(Authentication authentication, String follow)
      throws HttpException {
    if(authentication == null) {
      throw new HttpException("Not logged in.", HttpStatus.UNAUTHORIZED);
    }
    String username = authentication.getName();
    if(!rateLimiter.interact(username)) {
      throw new HttpException("Too many requests.", HttpStatus.TOO_MANY_REQUESTS);
    }
    if(username.equals(follow)) {
      throw new HttpException("You cannot unfollow yourself.", HttpStatus.BAD_REQUEST);
    }
  }
  
}
