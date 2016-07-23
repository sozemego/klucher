package com.soze.front.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/")
public class FrontController {

  @RequestMapping(method = RequestMethod.GET)
  public String getFrontPage(Authentication authentication) {
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      return "redirect:dashboard";
    }
    return "front";
  }

}
