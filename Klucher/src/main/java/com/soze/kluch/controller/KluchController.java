package com.soze.kluch.controller;

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

import com.soze.kluch.exceptions.AlreadyPostedException;
import com.soze.kluch.exceptions.InvalidKluchContentException;
import com.soze.kluch.service.KluchService;

@Controller
public class KluchController {
  
  private static final Logger log = LoggerFactory.getLogger(KluchController.class);
  private final KluchService kluchService;
  
  @Autowired
  public KluchController(KluchService kluchService) {
    this.kluchService = kluchService;
  }

  @RequestMapping(value = "/kluch", method = RequestMethod.POST)
  public ResponseEntity<String> postKluch(Authentication authentication, @RequestParam String kluch) {
    if(authentication == null) {
      return new ResponseEntity<String>(HttpStatus.UNAUTHORIZED);
    }
    String username = authentication.getName();
    try {
      kluchService.post(username, kluch);
    } catch (AlreadyPostedException e) {
      log.info("User [{}] last Kluch content equals this one [{}].", username, kluch, e);
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    } catch (InvalidKluchContentException e) {
      log.info("User [{}] tried to post a Kluch with invalid content [{}].", username, kluch, e);
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    } catch (IllegalArgumentException e) {
      log.info("Username [{}] is null or empty.", username, e);
      return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
    } 
    return new ResponseEntity<String>(HttpStatus.OK);
  }
  
}
