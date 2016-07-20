package com.soze.userpage.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class UserPageController {

  @RequestMapping(value = "/u/{username}", method = RequestMethod.GET)
  public String userPage(Authentication authentication, @PathVariable String username) {
    
    return "user";
  }
  
  
}
