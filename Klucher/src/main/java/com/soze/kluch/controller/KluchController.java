package com.soze.kluch.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class KluchController {

  @RequestMapping(value = "/kluch", method = RequestMethod.POST)
  public ResponseEntity<String> postKluch(Authentication authentication, String kluch) {
    
    return new ResponseEntity<String>(HttpStatus.OK);
  }
  
}
