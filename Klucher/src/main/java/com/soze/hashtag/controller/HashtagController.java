package com.soze.hashtag.controller;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HashtagController {

  @RequestMapping(value = "/hashtag/{hashtag}", method = RequestMethod.GET)
  public String getHashtag(@PathVariable String hashtag, Authentication authentication, Model model) {
  	
    model.addAttribute("hashtag", hashtag.toLowerCase());
    
    boolean loggedIn = authentication != null && !(authentication instanceof AnonymousAuthenticationToken);
    model.addAttribute("loggedIn", loggedIn);
    if(loggedIn) {
      model.addAttribute("username", authentication.getName());
    }
    
    return "hashtag";
  }
  
  @RequestMapping(value = "/hashtag", method = RequestMethod.GET)
  public String handleRedirect() {
    return "redirect:/dashboard";
  }
    
}
