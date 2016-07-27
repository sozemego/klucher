package com.soze.feed.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.common.exceptions.HttpException;
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
      @RequestParam(required = true) Long timestamp)
      throws Exception {
    if (authentication == null) {
      log.info("Anonymous user polled a feed.");
      return false; //TODO omg this cannot be like this
    }
    if(timestamp == null) {
      log.info("User [{}] tried to poll feed but did not supply a timestamp.", authentication.getName());
      return false;
    }
    String username = authentication.getName();
    boolean existsAfter = feedConstructor.existsFeedAfter(username, timestamp, false);
    log.info("User [{}] polled their feed, with timestamp [{}] and feed constructor returned [{}].", username, timestamp, existsAfter);
    return existsAfter;
  }
  
  @RequestMapping(value = "/feed/poll/{username}", method = RequestMethod.GET)
  @ResponseBody
  public Boolean pollFeed(@RequestParam Long timestamp, @PathVariable String username)
      throws Exception {
    boolean existsAfter = feedConstructor.existsFeedAfter(username, timestamp, true);
    log.info("Someone polled their feed, with timestamp [{}] and feed constructor returned [{}].", timestamp, existsAfter);
    return existsAfter;
  }

  @RequestMapping(value = "/feed", method = RequestMethod.GET)
  @ResponseBody
  public Feed getFeed(Authentication authentication,
      @RequestParam Long timestamp,
      @RequestParam(required = false) String direction) throws Exception {
    if (authentication == null) {
      throw new HttpException("Not logged in.", HttpStatus.UNAUTHORIZED);
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
      feed = feedConstructor.constructFeed(username, timestamp, false);
    } else if (direction.equals("before")) {
      feed = feedConstructor.constructFeedAfter(username, timestamp, false);
    }
    return feed;
  }
  
  @RequestMapping(value = "/feed/{username}", method = RequestMethod.GET)
  @ResponseBody
  public Feed getFeed(@RequestParam Long timestamp,
      @RequestParam(required = false) String direction,
      @PathVariable String username) throws Exception {
    if (timestamp == null) {
      timestamp = Long.MAX_VALUE;
    }
    if (direction == null || direction.isEmpty()) {
      direction = "after";
    }
    log.info("Trying to construct feed of user [{}] with direction [{}] and timestamp [{}]", username, direction, timestamp);
    Feed feed = null;
    if (direction.equalsIgnoreCase("after")) {
      feed = feedConstructor.constructFeed(username, timestamp, true);
    } else if (direction.equals("before")) {
      feed = feedConstructor.constructFeedAfter(username, timestamp, true);
    }
    return feed;
  }

}
