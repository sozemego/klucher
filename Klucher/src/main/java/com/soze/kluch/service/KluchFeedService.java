package com.soze.kluch.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.InvalidTimestampException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.follow.dao.FollowDao;
import com.soze.follow.model.Follow;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.FeedRequest;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.model.KluchFeedElement;
import com.soze.kluch.model.KluchUserView;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class KluchFeedService {
	
	private final static int ELEMENTS_PER_REQUEST = 30;
	private final PageRequest next = new PageRequest(0, ELEMENTS_PER_REQUEST,
			new Sort(new Order(Direction.DESC, "timestamp")));
	private final PageRequest previous = new PageRequest(0, ELEMENTS_PER_REQUEST,
			new Sort(new Order(Direction.ASC, "timestamp")));
	private final PageRequest exists = new PageRequest(0, 1);
	private final KluchDao kluchDao;
	private final UserDao userDao;
	private final FollowDao followDao;

	@Autowired
	public KluchFeedService(KluchDao kluchDao, UserDao userDao, FollowDao followDao) {
		this.kluchDao = kluchDao;
		this.userDao = userDao;
		this.followDao = followDao;
	}

	/**
	 * Returns a {@link Feed} for a given username. This method simply returns a
	 * finite (currently up to 30) amount of {@link KluchFeedElement}s. These
	 * objects wrap a single {@link Kluch} and relevant user (kluch's author) data
	 * contained in a {@link KluchUserView} object. FeedRequest encapsulates
	 * the direction of the feed (next/previous).
	 * If onlyForUser is true,returns only this user's {@link Kluch}s, otherwise it includes all users
	 * they follow too. Returned Kluchs are sorted by timestamp depending on the
	 * direction (descending for before and ascending for after). Relevant user
	 * data is attached to every <code>Kluch</code>. If no
	 * kluchs are found, returns an empty feed.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param username
	 *          name of the user for which we want to construct the feed
	 * @param feedRequest
	 * 					encapsulates feed direction (previous/next) and id used in feed creation
	 * @param onlyForUser
	 *          flag which specifies whether Kluchs in the feed should only be for
	 *          a given user (true) or for their followers too (false)
	 * @return <code>Feed</code> of <code>KluchFeedElement</code>s. Empty if id is invalid
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	public Feed<KluchFeedElement> constructFeed(String username, FeedRequest feedRequest, boolean onlyForUser) throws UserDoesNotExistException, NullOrEmptyException {
		if (feedRequest.getFeedDirection() == FeedDirection.PREVIOUS) {
			return constructFeedPrevious(username, feedRequest.getId(), onlyForUser);
		}
		if (feedRequest.getFeedDirection() == FeedDirection.NEXT) {
			return constructFeedNext(username, feedRequest.getId(), onlyForUser);
		}
		return new Feed<>(new ArrayList<>(0), null, null, 0);
	}

	/**
	 * Returns a {@link Feed} for a given username. This method simply returns a
	 * finite (currently up to 30) amount of {@link KluchFeedElement}s. These
	 * objects wrap a single {@link Kluch} and relevant user (kluch's author) data
	 * contained in a {@link KluchUserView} object. Retrieves <code>Kluchs</code>
	 * posted before (earlier than) a given <code>id</code>.
	 * If onlyForUser is true, returns only this user's
	 * {@link Kluch}s, otherwise it includes all users they follow too. Returned
	 * Kluchs are sorted by timestamp in descending order. Relevant user data is
	 * attached to every <code>Kluch</code>. If no kluchs are found, returns an
	 * empty feed.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param username
	 *          name of the user for which we want to construct the feed
	 * @param id
	 *          id of the element before which you want the feed to start
	 * @param onlyForUser
	 *          flag which specifies whether Kluchs in the feed should only be for
	 *          a given user (true) or for their followers too (false)
	 * @return <code>Feed</code> of <code>KluchFeedElement</code>s
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	private Feed<KluchFeedElement> constructFeedPrevious(String username, long id, boolean onlyForUser)
			throws UserDoesNotExistException, NullOrEmptyException {
		User user = getUser(username);
		List<Long> authorIds = getIdsOfAuthors(user.getId(), onlyForUser);
		Page<Kluch> kluchs = kluchDao.findByAuthorIdInAndIdGreaterThan(authorIds, id, previous);
		Feed<KluchFeedElement> feed = constructFeed(kluchs);
		return feed;
	}

	/**
	 * Returns a {@link Feed} for a given username. This method simply returns a
	 * finite (currently up to 30) amount of {@link KluchFeedElement}s. These
	 * objects wrap a single {@link Kluch} and relevant user (kluch's author) data
	 * contained in a {@link KluchUserView} object. Retrieves <code>Kluchs</code>
	 * posted before (earlier than) a given <code>id</code>.
	 * If onlyForUser is true, returns only this user's {@link Kluch}s,
	 * otherwise it includes all users they follow too. Returned Kluchs are sorted
	 * by timestamp in ascending order. Relevant user data is attached to every
	 * <code>Kluch</code>. If no kluchs are found, returns an empty feed.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param username
	 *          name of the user for which we want to construct the feed
	 * @param id
	 *          id of the element after which you want the feed to start
	 * @param onlyForUser
	 *          flag which specifies whether Kluchs in the feed should only be for
	 *          a given user (true) or for their followers too (false)
	 * @return <code>Feed</code> of <code>KluchFeedElement</code>s
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	private Feed<KluchFeedElement> constructFeedNext(String username, long id, boolean onlyForUser)
			throws UserDoesNotExistException, NullOrEmptyException {
		User user = getUser(username);
		List<Long> authorIds = getIdsOfAuthors(user.getId(), onlyForUser);
		Page<Kluch> kluchs = kluchDao.findByAuthorIdInAndIdLessThan(authorIds, id, next);
		Feed<KluchFeedElement> feed = constructFeed(kluchs);
		return feed;
	}
	
	/**
	 * Checks if there exist Kluchs posted after (later) given kluch id.
	 * 
	 * @param username
	 *          name of the user for which we want to poll the feed
	 * @param feedRequest
	 *          encapsulates feed direction (previous/next) and id used in feed creation
	 * @return true if there are Kluchs posted after given timestamp (in epoch
	 *         millis)
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	public boolean existsFeedAfter(String username, FeedRequest feedRequest, boolean onlyForUser)
			throws UserDoesNotExistException, NullOrEmptyException {
		User user = getUser(username);
		List<Long> authorIds = getIdsOfAuthors(user.getId(), onlyForUser);
		Page<Kluch> kluchs = kluchDao.findByAuthorIdInAndIdGreaterThan(authorIds, feedRequest.getId(), exists);
		return kluchs.hasContent();
	}
	
	/**
	 * Returns a feed of {@link KluchFeedElement}s which contain a mention of a
	 * given user and were all posted before (earlier than)
	 * <code>beforeTimestamp</code>
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param username
	 *          username for who we wish to retrieve Kluchs with mentions
	 * @param feedRequest
	 *          encapsulates feed direction (previous/next) and id used in feed creation
	 * @return a feed of {@link KluchFeedElement}
	 * @throws NullOrEmptyException
	 *           if username is null or empty
	 * @throws UserDoesNotExistException
	 *           if user with <code>username</code> does not exist
	 */
	public Feed<KluchFeedElement> getMentions(String username, FeedRequest feedRequest)
			throws NullOrEmptyException, UserDoesNotExistException {
		getUser(username);
		Page<Kluch> kluchs = kluchDao.findByMentionsInAndIdLessThan(username, feedRequest.getId(), next);
		Feed<KluchFeedElement> feed = constructFeed(kluchs);
		return feed;
	}
	
	/**
	 * Returns a {@link Feed} for a hashtag. This method simply returns a finite
	 * (currently up to 30) amount of {@link KluchFeedElement}s. These objects
	 * wrap a single {@link Kluch} which contains this hashtag and relevant user
	 * (kluch's author) data contained in a {@link KluchUserView} object.
	 * Retrieves <code>Kluch</code>s posted before (earlier than) given id.
 	 *If a given hashtag has never been posted, returns an empty feed.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param hashtagText
	 *          does not have to contain pound character as the first character
	 *          (but it can)
	 * @param feedRequest
	 * 					encapsulates feed direction (previous/next) and id used in feed creation
	 * @return <code>Feed</code> of <code>Kluchs</code>
	 * @throws InvalidTimestampException
	 *           if timestamp is less than 0
	 * @throws NullOrEmptyException
	 *           if <code>hashtagText</code> is null or empty
	 */
	public Feed<KluchFeedElement> constructHashtagFeed(String hashtagText, FeedRequest feedRequest)
			throws UserDoesNotExistException, NullOrEmptyException {
		boolean hasPoundCharacter = hashtagText.startsWith("#");
		if (hasPoundCharacter) {
			hashtagText = hashtagText.substring(1);
		}
		Page<Kluch> kluchs = kluchDao.findByHashtagsInAndIdLessThan(hashtagText, feedRequest.getId(), next);
		Feed<KluchFeedElement> feed = constructFeed(kluchs);
		return feed;
	}
	
	/**
	 * Validates username and checks if user exists. If it does, returns the
	 * {@link User}.
	 * 
	 * @param username
	 * @return
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException
	 *           if username with given <code>username</code> doesn't exist
	 */
	private User getUser(String username) throws UserDoesNotExistException, NullOrEmptyException {
		if (username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
		User user = userDao.findOne(username);
		if (user == null) {
			throw new UserDoesNotExistException("There is no user named " + username);
		}
		return user;
	}
	
	/**
	 * Returns a list of authors this user would see kluchs of (so this user's
	 * kluchs and their followers' kluchs).
	 * 
	 * @param user
	 * @param onlyForUser
	 *          whether the list of authors should include only the user or also
	 *          users they follow
	 * @return
	 */
	private List<Long> getIdsOfAuthors(long userId, boolean onlyForUser) {
		if (onlyForUser) {
			return Arrays.asList(userId);
		}
		List<Follow> followees = followDao.findAllByFollowerId(userId);
		List<Long> followeeIds = followees.stream()
				.map(f -> f.getFolloweeId())
				.collect(Collectors.toList());
		followeeIds.add(userId);
		return followeeIds;
	}
	
	/**
	 * Constructs a feed from this page.
	 * @param page
	 * @return
	 */
	private Feed<KluchFeedElement> constructFeed(Page<Kluch> page) {
		long totalElements = (int) page.getTotalElements();
		Long previous = null;
		Long next = null;
		List<Kluch> kluchs = page.getContent();
		if (kluchs.size() > 0) {
			previous = kluchs.get(0).getTimestamp().getTime();
			next = kluchs.get(kluchs.size() - 1).getTimestamp().getTime();
		}
		if(page.isLast()) {
			next = null;
		}
		return new Feed<>(convertKluchsToFeedElements(kluchs), previous, next, totalElements);
	}
	
	/**
	 * Finds all {@link User}s which posted given <code>Kluch</code>s and attaches
	 * relevant user data of the author in a form of {@link KluchUserView}.
	 * 
	 * @param kluchs
	 *          list of valid Kluchs
	 * @return a list of <code>KluchFeedElement</code>
	 * @throws UserDoesNotExistException
	 *           if any of the Kluchs' authors don't exist
	 */
	private List<KluchFeedElement> convertKluchsToFeedElements(List<Kluch> kluchs) throws UserDoesNotExistException {
		Set<Long> authorIds = kluchs.stream().map(k -> k.getAuthorId()).collect(Collectors.toSet());
		List<User> users = userDao.findAll(authorIds);
		List<KluchFeedElement> feedElements = new ArrayList<>(kluchs.size());
		for (Kluch k : kluchs) {
			feedElements.add(new KluchFeedElement(k, getKluchUserView(getUserForKluch(k, users))));
		}
		/**
		Collections.sort(feedElements, (k1, k2) -> {
			long k1Time = k1.getKluch().getTimestamp().getTime();
			long k2Time = k2.getKluch().getTimestamp().getTime();
			if (k1Time < k2Time) {
				return 1;
			}
			if (k1Time > k2Time) {
				return -1;
			}
			return 0;
		});
		*/
		return feedElements;
	}
	
	private User getUserForKluch(Kluch kluch, List<User> users) throws UserDoesNotExistException {
		for (User user : users) {
			if (user.getId().equals(kluch.getAuthorId())) {
				return user;
			}
		}
		throw new UserDoesNotExistException("" + kluch.getAuthorId());
	}

	private KluchUserView getKluchUserView(User user) {
		return new KluchUserView(user.getUsername(), user.getAvatarPath());
	}
	
}
