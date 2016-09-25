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

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.feed.model.Feed;
import com.soze.feed.model.KluchFeedElement;
import com.soze.feed.service.FeedConstructor;
import com.soze.feed.service.FeedConstructor.FeedDirection;
import com.soze.notification.model.FollowNotification;

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
			@RequestParam Long timestamp,
			@PathVariable String username)
			throws Exception {
		
		String authenticatedUsername = authentication == null ? null : authentication.getName();
		boolean onlyForUser = isOnlyForUser(authentication, username, authenticatedUsername);
		boolean existsAfter = feedConstructor.existsFeedAfter(username, timestamp, onlyForUser);

		log.info("User [{}] polled feed for user [{}], with timestamp [{}] and feed constructor returned [{}].",
				authenticatedUsername, username, timestamp, existsAfter);
		
		return existsAfter;
	}

	@RequestMapping(value = "/feed/{username}", method = RequestMethod.GET)
	@ResponseBody
	public Feed<KluchFeedElement> getFeed(Authentication authentication,
			@PathVariable String username,
			@RequestParam Long timestamp,
			@RequestParam(required = false) String direction)
			throws Exception {
		
		String authenticatedUsername = authentication == null ? null : authentication.getName();
		boolean onlyForUser = isOnlyForUser(authentication, username, authenticatedUsername);
		FeedDirection feedDirection = FeedDirection.AFTER;
		if ("before".equalsIgnoreCase(direction)) {
			feedDirection = FeedDirection.BEFORE;
		}
		
		log.info("User [{}] requested to construct a feed for user [{}], with timestamp [{}] and direction [{}]",
				authenticatedUsername, username, timestamp, direction);
		return feedConstructor.constructFeed(username, timestamp, onlyForUser, feedDirection);
	}
	
	@RequestMapping(value = "/feed/mentions", method = RequestMethod.GET)
	@ResponseBody
	public Feed<KluchFeedElement> getKluchsWithMentions(Authentication authentication, @RequestParam Long timestamp) {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		
		Feed<KluchFeedElement> feed = feedConstructor.getMentions(username, timestamp);
		log.info("User [{}] requested their mentions feed with timestamp [{}]. Feed returned : [{}]",
				username, timestamp, feed);
		return feed;
	}
	
	@RequestMapping(value = "/feed/follows", method = RequestMethod.GET)
	@ResponseBody
	public Feed<FollowNotification> getFollowNotifications(Authentication authentication, @RequestParam Long timestamp) {
		if(authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		
		Feed<FollowNotification> feed = feedConstructor.getFollowNotifications(username, timestamp);
		log.info("User [{}] requested their mentions feed with timestamp [{}]. Feed returned : [{}]",
				username, timestamp, feed);
		return feed;
	}

  @RequestMapping(value = "/feed/hashtag/{hashtag}", method = RequestMethod.GET)
  @ResponseBody
  public Feed<KluchFeedElement> getHashtagPage(@PathVariable String hashtag, @RequestParam Long timestamp) {
  	Feed<KluchFeedElement> feed = feedConstructor.constructHashtagFeed(hashtag.toLowerCase(), timestamp);
  	log.info("Someone requested feed of hashtags for hashtag [{}] with timestamp [{}]. Feed returned [{}].", hashtag, timestamp, feed);
  	return feed;
  }

	/**
	 * If an authenticated user polls their own feed, returns false. Otherwise,
	 * returns true.
	 * 
	 * @param authentication
	 * @param username
	 * @param authenticatedUsername
	 * @return
	 */
	private boolean isOnlyForUser(Authentication authentication, String username, String authenticatedUsername) {
		boolean onlyForUser = true;
		if (authentication != null && username.equals(authenticatedUsername)) {
			onlyForUser = false;
		}
		return onlyForUser;
	}

}
