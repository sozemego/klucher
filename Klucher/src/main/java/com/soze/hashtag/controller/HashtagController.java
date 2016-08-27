package com.soze.hashtag.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.feed.model.Feed;
import com.soze.feed.service.FeedConstructor;

@Controller
public class HashtagController {
  
  private final FeedConstructor feedConstructor;
  
  @Autowired
  public HashtagController(FeedConstructor feedConstructor) {
    this.feedConstructor = feedConstructor;
  }

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
  
  @RequestMapping(value = "/hashtag/feed/{hashtag}", method = RequestMethod.GET)
  @ResponseBody
  public Feed getHashtagPage(@PathVariable String hashtag, @RequestParam Long timestamp) {
    return feedConstructor.constructHashtagFeed(hashtag.toLowerCase(), timestamp);
  }
  
  @RequestMapping(value = "/hashtag", method = RequestMethod.GET)
  public String handleRedirect() {
    return "redirect:/dashboard";
  }
    
}
