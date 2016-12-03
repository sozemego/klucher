package com.soze.kluch.controller;

import java.util.Optional;

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

@Controller
public class KluchController {

	private static final Logger LOG = LoggerFactory.getLogger(KluchController.class);
	private final KluchService kluchService;
	private final KluchFeedService kluchFeedService;

	@Autowired
	public KluchController(KluchService kluchService,
			KluchFeedService kluchFeedService) {
		this.kluchService = kluchService;
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
		kluchService.deleteKluch(username, kluchId);
		return new ResponseEntity<Object>(HttpStatus.OK);
	}

	@RequestMapping(value = "/kluch/{username}", method = RequestMethod.GET)
	@ResponseBody
	public Feed<KluchFeedElement> getFeed(Authentication authentication, @PathVariable String username,
			@RequestParam(required = false) Long previous,
			@RequestParam(required = false) Long next) throws Exception {

		String authenticatedUsername = authentication == null ? null : authentication.getName();
		boolean onlyForUser = isOnlyForUser(authentication, username, authenticatedUsername);

		FeedRequest feedRequest = createFeedRequest(previous, next, authenticatedUsername);

		LOG.info("User [{}] requested to construct a feed for user [{}], with feed request [{}].",
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
		
		FeedRequest feedRequest = createFeedRequest(previous, next, authenticatedUsername);
		boolean existsAfter = kluchFeedService.existsFeedAfter(username, feedRequest, onlyForUser);
		
		LOG.info("User [{}] polled feed for user [{}], with feedRequest [{}] and feed constructor returned [{}].",
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
		FeedRequest feedRequest = createFeedRequest(null, next, username);
		Feed<KluchFeedElement> feed = kluchFeedService.getMentions(username, feedRequest);
		LOG.info("User [{}] requested their mentions feed with feed request [{}]. Feed returned : [{}]", username, feedRequest, feed);
		return feed;
	}

	@RequestMapping(value = "/kluch/hashtag/{hashtag}", method = RequestMethod.GET)
	@ResponseBody
	public Feed<KluchFeedElement> getHashtagPage(Authentication authentication, @PathVariable String hashtag,
			@RequestParam(required = false) Long next) {
		String username = authentication == null ? null : authentication.getName();
		FeedRequest feedRequest = createFeedRequest(null, next, username);
		Feed<KluchFeedElement> feed = kluchFeedService.constructHashtagFeed(hashtag.toLowerCase(), feedRequest);
		LOG.info("Someone requested feed of hashtags for hashtag [{}] with feed request [{}]. Feed returned [{}].", hashtag, feedRequest,
				feed);
		return feed;
	}
	
	@RequestMapping(value = "/kluch/like", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> like(Authentication authentication, @RequestParam Long kluchId) {
		if (authentication == null) {
			throw new NotLoggedInException();
		}
		kluchService.likeKluch(authentication.getName(), kluchId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@RequestMapping(value = "/kluch/unlike", method = RequestMethod.POST)
	@ResponseBody
	public ResponseEntity<Object> unlike(Authentication authentication, @RequestParam Long kluchId) {
		if (authentication == null) {
			throw new NotLoggedInException();
		}
		kluchService.unlikeKluch(authentication.getName(), kluchId);
		return new ResponseEntity<>(HttpStatus.OK);
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

	/**
	 * Constructs a FeedRequest object based on the supplied parameters.
	 * @param previous id of the element. Can be null
	 * @param next id of the element. Can be null
	 * @param sourceUsername username of the requesting user. Can be null
	 * @return FeedRequest
	 */
	private FeedRequest createFeedRequest(Long previous, Long next, String sourceUsername) {
  	Optional<String> source = Optional.ofNullable(sourceUsername);
		if (previous != null) {
			return new FeedRequest(FeedDirection.PREVIOUS, previous, source);
		}
		return new FeedRequest(FeedDirection.NEXT, next, source);
	}

}
