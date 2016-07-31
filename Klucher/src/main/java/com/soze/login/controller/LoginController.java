package com.soze.login.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class LoginController {

  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public String getLogin(Authentication authentication) {
    if (authentication != null && !(authentication instanceof AnonymousAuthenticationToken)) {
      return "redirect:/dashboard";
    }
    return "login";
  }

}
