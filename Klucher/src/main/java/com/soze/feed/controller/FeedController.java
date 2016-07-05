package com.soze.feed.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.feed.model.Feed;
import com.soze.feed.service.FeedConstructor;

@Controller
public class FeedController {

  private static final Logger log = LoggerFactory
      .getLogger(FeedController.class);

  private final FeedConstructor feedConstructor;

  @Autowired
  public FeedController(FeedConstructor feedConstructor) {
    this.feedConstructor = feedConstructor;
  }

  @RequestMapping(value = "/feed/poll", method = RequestMethod.GET)
  @ResponseBody
  public Boolean pollFeed(Authentication authentication,
      HttpServletResponse response, @RequestParam Long timestamp)
      throws Exception {
    if (authentication == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "You are not logged in.");
      return false;
    }
    if(timestamp == null) {
      response.sendError(HttpServletResponse.SC_BAD_REQUEST,
          "Timestamp cannot be null.");
      return false;
    }
    String username = authentication.getName();
    return feedConstructor.existsFeedAfter(username, timestamp);
  }

  @RequestMapping(value = "/feed", method = RequestMethod.GET)
  @ResponseBody
  public Feed getFeed(Authentication authentication,
      HttpServletResponse response, @RequestParam Long timestamp,
      @RequestParam(required = false) String direction) throws Exception {
    if (authentication == null) {
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "You are not logged in.");
      return null;
    }
    if (timestamp == null) {
      timestamp = Long.MAX_VALUE;
    }
    if (direction == null || direction.isEmpty()) {
      direction = "after";
    }
    String username = authentication.getName();
    log.info("Trying to construct a feed for user [{}]. timestamp = [{}], direction = [{}]",
        username, timestamp, direction);
    Feed feed = null;
    if (direction.equalsIgnoreCase("after")) {
      feed = feedConstructor.constructFeed(username, timestamp);
    } else if (direction.equals("before")) {
      feed = feedConstructor.constructFeedAfter(username, timestamp);
    }
    return feed;
  }

}
