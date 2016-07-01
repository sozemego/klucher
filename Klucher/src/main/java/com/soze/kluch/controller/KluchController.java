package com.soze.kluch.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.soze.kluch.service.KluchService;

@Controller
public class KluchController {
  
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
    boolean postSuccessful = kluchService.post(authentication.getName(), kluch);
    if(!postSuccessful) {
      HttpHeaders headers = new HttpHeaders();
      headers.add("message", "Problem sharing your kluch.");
      return new ResponseEntity<String>(headers, HttpStatus.BAD_REQUEST);
    } 
    return new ResponseEntity<String>(HttpStatus.OK);
  }
  
}
