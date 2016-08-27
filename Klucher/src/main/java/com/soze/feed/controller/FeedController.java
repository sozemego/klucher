package com.soze.feed.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.feed.model.Feed;
import com.soze.feed.service.FeedConstructor;

@Controller
public class FeedController {

  private static final Logger log = LoggerFactory.getLogger(FeedController.class);

  private final FeedConstructor feedConstructor;

  @Autowired
  public FeedController(FeedConstructor feedConstructor) {
    this.feedConstructor = feedConstructor;
  }

  @RequestMapping(value = "/feed/poll/{username}", method = RequestMethod.GET)
  @ResponseBody
  public Boolean pollFeed(Authentication authentication,
      @RequestParam(required = true) Long timestamp, @PathVariable String username)
      throws Exception {
    boolean onlyForUser = true;
    String authenticatedUsername = authentication == null ? null : authentication.getName();
    if (authentication != null && username.equals(authenticatedUsername)) {
      onlyForUser = false;
    }
    boolean existsAfter = feedConstructor.existsFeedAfter(username, timestamp, onlyForUser);
    log.info("User [{}] polled their feed, with timestamp [{}] and feed constructor returned [{}].",
        authenticatedUsername, timestamp, existsAfter);
    return existsAfter;
  }

  @RequestMapping(value = "/feed/{username}", method = RequestMethod.GET)
  @ResponseBody
  public Feed getFeed(Authentication authentication, @PathVariable String username,
      @RequestParam(required = true) Long timestamp,
      @RequestParam(required = false) String direction) throws Exception {
    boolean onlyForUser = true;
    String authenticatedUsername = authentication == null ? null : authentication.getName();
    if (authentication != null && username.equals(authenticatedUsername)) {
      onlyForUser = false;
    }
    if (direction == null || direction.isEmpty()) {
      direction = "after";
    }
    log.info("Trying to construct a feed for user [{}]. timestamp = [{}], direction = [{}]",
        username, timestamp, direction);
    return feedConstructor.constructFeed(username, timestamp, onlyForUser, direction);
  }

}
