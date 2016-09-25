package com.soze.feed.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import com.soze.feed.model.Feed;
import com.soze.feed.model.KluchFeedElement;
import com.soze.hashtag.dao.HashtagDao;
import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.model.KluchUserView;
import com.soze.notification.model.FollowNotification;
import com.soze.notification.model.MentionNotification;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * A service which is responsible for creating Feeds of kluch for users. This
 * includes feeds of users' posts and feeds of Kluchs containing a certain
 * hashtag.
 * 
 * @author sozek
 *
 */
@Service
public class FeedConstructor {

	private final static int ELEMENTS_PER_REQUEST = 30;
	private final PageRequest before = new PageRequest(0, ELEMENTS_PER_REQUEST,
			new Sort(new Order(Direction.DESC, "timestamp")));
	private final PageRequest after = new PageRequest(0, ELEMENTS_PER_REQUEST,
			new Sort(new Order(Direction.ASC, "timestamp")));
	private final PageRequest exists = new PageRequest(0, 1);
	private final KluchDao kluchDao;
	private final UserDao userDao;
	private final HashtagDao hashtagDao;

	@Autowired
	public FeedConstructor(KluchDao kluchDao, UserDao userDao, HashtagDao hashtagDao) {
		this.kluchDao = kluchDao;
		this.userDao = userDao;
		this.hashtagDao = hashtagDao;
	}

	/**
	 * Returns a {@link Feed} for a given username. This method simply returns a
	 * finite (currently up to 30) amount of {@link KluchFeedElement}s. These
	 * objects wrap a single {@link Kluch} and relevant user (kluch's author) data
	 * contained in a {@link KluchUserView} object. Retrieves <code>Kluchs</code>
	 * posted before (earlier than) or after (later than) a given
	 * <code>timestamp</code> (milliseconds unix time). If onlyForUser is true,
	 * returns only this user's {@link Kluch}s, otherwise it includes all users
	 * they follow too. Returned Kluchs are sorted by timestamp depending on the
	 * direction (descending for before and ascending for after). Relevant user
	 * data is attached to every <code>Kluch</code>. If direction is invalid or no
	 * kluchs are found, returns an empty feed.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param username
	 *          name of the user for which we want to construct the feed
	 * @param timestamp
	 *          unix milliseconds before (or after) which you want to construct
	 *          the feed
	 * @param onlyForUser
	 *          flag which specifies whether Kluchs in the feed should only be for
	 *          a given user (true) or for their followers too (false)
	 * @param direction
	 *          specifies whether you want to retrieve kluchs "after" or "before"
	 *          timestamp
	 * @return <code>Feed</code> of <code>KluchFeedElement</code>s
	 * @throws InvalidTimestampException
	 *           if timestamp is less than 0
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	public Feed<KluchFeedElement> constructFeed(String username, long timestamp, boolean onlyForUser,
			FeedDirection direction) throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {
		if (direction == FeedDirection.BEFORE) {
			return constructFeed(username, timestamp, onlyForUser);
		}
		if (direction == FeedDirection.AFTER) {
			return constructFeedAfter(username, timestamp, onlyForUser);
		}
		return new Feed<>(new ArrayList<>(0), null, null, 0);
	}

	/**
	 * Returns a {@link Feed} for a given username. This method simply returns a
	 * finite (currently up to 30) amount of {@link KluchFeedElement}s. These
	 * objects wrap a single {@link Kluch} and relevant user (kluch's author) data
	 * contained in a {@link KluchUserView} object. Retrieves <code>Kluchs</code>
	 * posted before (earlier than) a given <code>timestamp</code> (milliseconds
	 * unix time). If onlyForUser is true, returns only this user's
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
	 * @param timestamp
	 *          unix milliseconds before which you want to construct the feed
	 * @param onlyForUser
	 *          flag which specifies whether Kluchs in the feed should only be for
	 *          a given user (true) or for their followers too (false)
	 * @return <code>Feed</code> of <code>KluchFeedElement</code>s
	 * @throws InvalidTimestampException
	 *           if timestamp is less than 0
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	public Feed<KluchFeedElement> constructFeed(String username, long beforeTimestamp, boolean onlyForUser)
			throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {
		validateTimestamp(beforeTimestamp);
		User user = getUser(username);
		List<String> authors = getListOfAuthors(user, onlyForUser);
		Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampLessThan(authors, new Timestamp(beforeTimestamp), before);
		Feed<KluchFeedElement> feed = constructFeed(kluchs);
		return feed;
	}

	/**
	 * Returns a {@link Feed} for a given username. This method simply returns a
	 * finite (currently up to 30) amount of {@link KluchFeedElement}s. These
	 * objects wrap a single {@link Kluch} and relevant user (kluch's author) data
	 * contained in a {@link KluchUserView} object. Retrieves <code>Kluchs</code>
	 * posted after (later than) a given <code>timestamp</code> (milliseconds unix
	 * time). If onlyForUser is true, returns only this user's {@link Kluch}s,
	 * otherwise it includes all users they follow too. Returned Kluchs are sorted
	 * by timestamp in ascending order. Relevant user data is attached to every
	 * <code>Kluch</code>. If no kluchs are found, returns an empty feed.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param username
	 *          name of the user for which we want to construct the feed
	 * @param timestamp
	 *          unix milliseconds after which you want to construct the feed
	 * @param onlyForUser
	 *          flag which specifies whether Kluchs in the feed should only be for
	 *          a given user (true) or for their followers too (false)
	 * @return <code>Feed</code> of <code>KluchFeedElement</code>s
	 * @throws InvalidTimestampException
	 *           if timestamp is less than 0
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	public Feed<KluchFeedElement> constructFeedAfter(String username, long afterTimestamp, boolean onlyForUser)
			throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {
		validateTimestamp(afterTimestamp);
		User user = getUser(username);
		List<String> authors = getListOfAuthors(user, onlyForUser);
		Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), after);
		Feed<KluchFeedElement> feed = constructFeed(kluchs);
		return feed;
	}

	/**
	 * Checks if there exist Kluchs posted after (later) given timestamp (in epoch
	 * millis).
	 * 
	 * @param username
	 *          name of the user for which we want to poll the feed
	 * @param afterTimestamp
	 *          returned Kluchs were posted after this epoch millis value
	 * @return true if there are Kluchs posted after given timestamp (in epoch
	 *         millis)
	 * @throws InvalidTimestampException
	 *           if timestamp is less than 0
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 */
	public boolean existsFeedAfter(String username, long afterTimestamp, boolean onlyForUser)
			throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {
		validateTimestamp(afterTimestamp);
		User user = getUser(username);
		List<String> authors = getListOfAuthors(user, onlyForUser);
		Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), exists);
		return kluchs.hasContent();
	}

	/**
	 * Returns a {@link Feed} for a hashtag. This method simply returns a finite
	 * (currently up to 30) amount of {@link KluchFeedElement}s. These objects
	 * wrap a single {@link Kluch} which contains this hashtag and relevant user
	 * (kluch's author) data contained in a {@link KluchUserView} object.
	 * Retrieves <code>Kluch</code>s posted before (earlier than) given timestamp
	 * (millis unix time). Returned Kluchs are sorted by timestamp in descending
	 * order. If a given hashtag has never been posted, returns an empty feed.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param hashtagText
	 *          does not have to contain pound character as the first character
	 *          (but it can)
	 * @param timestamp
	 * @return <code>Feed</code> of <code>Kluchs</code>
	 * @throws InvalidTimestampException
	 *           if timestamp is less than 0
	 * @throws NullOrEmptyException
	 *           if <code>hashtagText</code> is null or empty
	 */
	public Feed<KluchFeedElement> constructHashtagFeed(String hashtagText, long timestamp)
			throws UserDoesNotExistException, NullOrEmptyException {
		validateTimestamp(timestamp);
		Hashtag hashtag = getHashtag(hashtagText);
		if (hashtag == null) {
			return new Feed<>(new ArrayList<>(0), null, null, 0);
		}
		Page<Kluch> kluchs = kluchDao.findByHashtagsInAndTimestampLessThan(hashtag, new Timestamp(timestamp), before);
		Feed<KluchFeedElement> feed = constructFeed(kluchs);
		return feed;
	}

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
	 * Checks if given hashtagText is null or empty, and attempts to get a
	 * {@link Hashtag} with given text.
	 * 
	 * @param hashtagText
	 *          hashtagText with or without pound character as the first character
	 * @return
	 * @throws NullOrEmptyException
	 *           if <code>hashtagText</code> is null or empty
	 */
	private Hashtag getHashtag(String hashtagText) throws NullOrEmptyException {
		if (hashtagText == null || hashtagText.isEmpty()) {
			throw new NullOrEmptyException("Hashtag");
		}
		boolean hasPoundCharacter = hashtagText.startsWith("#");
		if (!hasPoundCharacter) {
			hashtagText = "#" + hashtagText;
		}
		return hashtagDao.findOne(hashtagText);
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
	 * @param beforeTimestamp
	 * @return a feed of {@link KluchFeedElement}
	 * @throws NullOrEmptyException
	 *           if username is null or empty
	 * @throws UserDoesNotExistException
	 *           if user with <code>username</code> does not exist
	 * @throws InvalidTimestampException
	 *           if <code>beforeTimestamp</code> is negative
	 */
	public Feed<KluchFeedElement> getMentions(String username, long beforeTimestamp)
			throws NullOrEmptyException, UserDoesNotExistException, InvalidTimestampException {
		validateTimestamp(beforeTimestamp);
		User user = getUser(username);
		List<MentionNotification> mentionNotifications = user.getMentionNotifications();
		List<MentionNotification> before = getMentionsBeforeTimestamp(mentionNotifications, beforeTimestamp);
		List<Long> kluchIds = extractKluchIdsFromMentions(before);
		List<Kluch> kluchs = getKluchs(kluchIds);
		Feed<KluchFeedElement> feed = constructFeed(kluchs, mentionNotifications.size());
		return feed;
	}

	private List<MentionNotification> getMentionsBeforeTimestamp(List<MentionNotification> notifications,
			long beforeTimestamp) {
		List<MentionNotification> before = notifications.stream()
				.filter(n -> n.getTimestamp() < beforeTimestamp)
				.limit(ELEMENTS_PER_REQUEST)
				.collect(Collectors.toList());
		return before;
	}

	private Feed<KluchFeedElement> constructFeed(List<Kluch> kluchs, long total) {
		if(total == 0) {
			return new Feed<>(new ArrayList<>(0), null, null, total);
		}
		Long previous = findOldestKluchTimestamp(kluchs);
		Long next = null;
		
		if(kluchs.size() == ELEMENTS_PER_REQUEST && total > kluchs.size()) {
			next = findEarliestKluchTimestamp(kluchs);
		}
		
		return new Feed<>(convertKluchsToFeedElements(kluchs), previous, next, total);
	}

	private long findOldestKluchTimestamp(List<Kluch> kluchs) {
		long oldest = 0;
		for (Kluch kluch : kluchs) {
			long time = kluch.getTimestamp().getTime();
			if (time > oldest) {
				oldest = time;
			}
		}
		return oldest;
	}

	private long findEarliestKluchTimestamp(List<Kluch> kluchs) {
		long earliest = Long.MAX_VALUE;
		for (Kluch kluch : kluchs) {
			long time = kluch.getTimestamp().getTime();
			if (time < earliest) {
				earliest = time;
			}
		}
		return earliest;
	}

	/**
	 * Finds up to 30 {@link FollowNotification}s for a given
	 * <code>username</code>, created after a given timestamp This method does not
	 * return {@link MentionNotification}s.
	 * 
	 * The {@link Feed} object contains relevant fields (next, previous, total)
	 * for paginating through the entire feed.
	 * 
	 * @param username
	 *          <code>username</code> for which we want to construct the
	 *          <code>feed</code>
	 * @return feed of notifications, sorted in descending order according to
	 *         timestamp (latest first)
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 * @throws InvalidTimestampException
	 *           if timestamp is negative
	 */
	public Feed<FollowNotification> getFollowNotifications(String username, long timestamp)
			throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		List<FollowNotification> followNotifications = user.getFollowNotifications();
		List<FollowNotification> notifications = getFollowNotificationsBeforeTimestamp(followNotifications, timestamp);
		Feed<FollowNotification> feed = constructFollowNotificationFeed(notifications, followNotifications.size());
		return feed;
	}

	private List<FollowNotification> getFollowNotificationsBeforeTimestamp(List<FollowNotification> followNotifications,
			long timestamp) {
		List<FollowNotification> notifications = followNotifications.stream()
				.filter(n -> n.getTimestamp() < timestamp)
				.limit(ELEMENTS_PER_REQUEST)
				.collect(Collectors.toList());
		return notifications;
	}

	private Feed<FollowNotification> constructFollowNotificationFeed(List<FollowNotification> notifications, long total) {
		if(total == 0) {
			return new Feed<>(new ArrayList<>(0), null, null, total);
		}
		Long previous = findOldestNotificationTimestamp(notifications);
		Long next = null;
		
		if(notifications.size() == ELEMENTS_PER_REQUEST && total > notifications.size()) {
			next = findEarliestNotificationTimestamp(notifications);
		}
		return new Feed<>(notifications, previous, next, total);
	}

	private long findOldestNotificationTimestamp(List<FollowNotification> notifications) {
		long oldest = 0;
		for (FollowNotification followNotification : notifications) {
			long time = followNotification.getTimestamp();
			if (time > oldest) {
				oldest = time;
			}
		}
		return oldest;
	}

	private long findEarliestNotificationTimestamp(List<FollowNotification> notifications) {
		long earliest = Long.MAX_VALUE;
		for (FollowNotification followNotification : notifications) {
			long time = followNotification.getTimestamp();
			if (time < earliest) {
				earliest = time;
			}
		}
		return earliest;
	}

	private List<Long> extractKluchIdsFromMentions(List<MentionNotification> notifications) {
		return notifications.stream()
				.map(n -> n.getKluchId())
				.collect(Collectors.toList());
	}

	private List<Kluch> getKluchs(List<Long> ids) {
		List<Kluch> kluchs = kluchDao.findAll((Iterable<Long>) ids);
		return kluchs;
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
	 * Validates timestamp value.
	 * 
	 * @param timestamp
	 * @throws InvalidTimestampException
	 *           if <code>timestamp</code> is negative
	 */
	private void validateTimestamp(long timestamp) throws InvalidTimestampException {
		if (timestamp < 0) {
			throw new InvalidTimestampException();
		}
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
	private List<String> getListOfAuthors(User user, boolean onlyForUser) {
		if (onlyForUser) {
			return Arrays.asList(user.getUsername());
		}
		List<String> authors = new ArrayList<>();
		authors.add(user.getUsername());
		authors.addAll(user.getFollowing());
		return authors;
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
		Set<String> usernames = kluchs.stream().map(k -> k.getAuthor()).collect(Collectors.toSet());
		List<User> users = userDao.findAll(usernames);
		List<KluchFeedElement> feedElements = new ArrayList<>(kluchs.size());
		for (Kluch k : kluchs) {
			feedElements.add(new KluchFeedElement(k, getKluchUserView(getUserForKluch(k, users))));
		}
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
		return feedElements;
	}

	private User getUserForKluch(Kluch kluch, List<User> users) throws UserDoesNotExistException {
		for (User user : users) {
			if (user.getUsername().equals(kluch.getAuthor())) {
				return user;
			}
		}
		throw new UserDoesNotExistException(kluch.getAuthor());
	}

	private KluchUserView getKluchUserView(User user) {
		return new KluchUserView(user.getUsername(), user.getAvatarPath());
	}

	public enum FeedDirection {
		AFTER, BEFORE;
	}

}
