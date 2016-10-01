package com.soze.kluch.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.soze.common.exceptions.NotLoggedInException;
import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.kluch.model.FeedRequest;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.model.KluchFeedElement;
import com.soze.kluch.service.KluchFeedService;
import com.soze.kluch.service.KluchService;
import com.soze.notification.service.NotificationService;

@Controller
public class KluchController {

	private static final Logger log = LoggerFactory.getLogger(KluchController.class);
	private final KluchService kluchService;
	private final NotificationService notificationService;
	private final KluchFeedService kluchFeedService;

	@Autowired
	public KluchController(KluchService kluchService, NotificationService notificationService,
			KluchFeedService kluchFeedService) {
		this.kluchService = kluchService;
		this.notificationService = notificationService;
		this.kluchFeedService = kluchFeedService;
	}

	@RequestMapping(value = "/kluch", method = RequestMethod.POST)
	@ResponseBody
	public Kluch postKluch(Authentication authentication, @RequestParam String kluchText) throws Exception {
		if (authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		Kluch kluch = kluchService.post(username, kluchText);
		notificationService.addNotifications(kluch.getMentions());
		return kluch;
	}

	@RequestMapping(value = "/kluch", method = RequestMethod.DELETE)
	@ResponseBody
	public ResponseEntity<Object> deleteKluch(Authentication authentication, @RequestParam Long kluchId)
			throws Exception {
		if (authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		Kluch kluch = kluchService.deleteKluch(username, kluchId);
		notificationService.removeNotifications(kluch.getMentions());
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@RequestMapping(value = "/kluch/{username}", method = RequestMethod.GET)
	@ResponseBody
	public Feed<KluchFeedElement> getFeed(Authentication authentication, @PathVariable String username,
			@RequestParam(required = false) Long previous,
			@RequestParam(required = false) Long next) throws Exception {

		String authenticatedUsername = authentication == null ? null : authentication.getName();
		boolean onlyForUser = isOnlyForUser(authentication, username, authenticatedUsername);

		FeedRequest feedRequest = createFeedRequest(previous, next);

		log.info("User [{}] requested to construct a feed for user [{}], with feed request [{}].",
				authenticatedUsername, username, feedRequest);
		return kluchFeedService.constructFeed(username, feedRequest, onlyForUser);
	}

	@RequestMapping(value = "/kluch/poll/{username}", method = RequestMethod.GET)
	@ResponseBody
	public Boolean pollKluchFeed(Authentication authentication,
			@RequestParam(required = false) Long previous,
			@RequestParam(required = false) Long next,
			@PathVariable String username) throws Exception {

		String authenticatedUsername = authentication == null ? null : authentication.getName();
		boolean onlyForUser = isOnlyForUser(authentication, username, authenticatedUsername);
		
		FeedRequest feedRequest = createFeedRequest(previous, next);
		boolean existsAfter = kluchFeedService.existsFeedAfter(username, feedRequest, onlyForUser);
		
		log.info("User [{}] polled feed for user [{}], with feedRequest [{}] and feed constructor returned [{}].",
				authenticatedUsername, username, feedRequest, existsAfter);

		return existsAfter;
	}

	@RequestMapping(value = "/kluch/mentions", method = RequestMethod.GET)
	@ResponseBody
	public Feed<KluchFeedElement> getKluchsWithMentions(Authentication authentication,
			@RequestParam(required = false) Long next) {
		if (authentication == null) {
			throw new NotLoggedInException();
		}
		String username = authentication.getName();
		FeedRequest feedRequest = createFeedRequest(null, next);
		Feed<KluchFeedElement> feed = kluchFeedService.getMentions(username, feedRequest);
		log.info("User [{}] requested their mentions feed with feed request [{}]. Feed returned : [{}]", username, feedRequest, feed);
		return feed;
	}

	@RequestMapping(value = "/kluch/hashtag/{hashtag}", method = RequestMethod.GET)
	@ResponseBody
	public Feed<KluchFeedElement> getHashtagPage(@PathVariable String hashtag,
			@RequestParam(required = false) Long next) {
		FeedRequest feedRequest = createFeedRequest(null, next);
		Feed<KluchFeedElement> feed = kluchFeedService.constructHashtagFeed(hashtag.toLowerCase(), feedRequest);
		log.info("Someone requested feed of hashtags for hashtag [{}] with feed request [{}]. Feed returned [{}].", hashtag, feedRequest,
				feed);
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

	private FeedRequest createFeedRequest(Long previous, Long next) {
		if (previous != null) {
			return new FeedRequest(FeedDirection.PREVIOUS, previous);
		}
		return new FeedRequest(FeedDirection.NEXT, next);
	}

}
