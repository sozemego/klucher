package com.soze.kluch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.common.exceptions.HttpException;
import com.soze.kluch.service.KluchService;
import com.soze.ratelimiter.service.RateLimiter;

@Controller
public class KluchController {
  
  private final KluchService kluchService;
  private final RateLimiter rateLimiter;
  
  @Autowired
  public KluchController(KluchService kluchService, RateLimiter rateLimiter) {
    this.kluchService = kluchService;
    this.rateLimiter = rateLimiter;
  }

  @RequestMapping(value = "/kluch", method = RequestMethod.POST)
  public ResponseEntity<String> postKluch(Authentication authentication, @RequestParam String kluch) throws Exception {
    if(authentication == null) {
      throw new HttpException("Not logged in.", HttpStatus.UNAUTHORIZED);
    }
    String username = authentication.getName();
    boolean canInteract = rateLimiter.interact(username);
    if(!canInteract) {
      throw new HttpException("Too many requests.", HttpStatus.TOO_MANY_REQUESTS);
    }
    kluchService.post(username, kluch);
    return new ResponseEntity<String>(HttpStatus.OK);
  }
  
}
