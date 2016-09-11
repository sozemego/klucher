package com.soze.feed.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * A service which is responsible for creating Feeds of kluch for users.
 * This includes feeds of users' posts and feeds of Kluchs containing a certain hashtag.
 * @author sozek
 *
 */
@Service
public class FeedConstructor {

  private static final int BEFORE_KLUCHS_PER_REQUEST = 30;
  private static final int AFTER_KLUCHS_PER_REQUEST = 30;
  private final Feed<KluchFeedElement> emptyFeed = new Feed<>(new ArrayList<>());
  private final PageRequest before = new PageRequest(0, BEFORE_KLUCHS_PER_REQUEST, new Sort(new Order(Direction.DESC, "timestamp")));
  private final PageRequest after = new PageRequest(0, AFTER_KLUCHS_PER_REQUEST, new Sort(new Order(Direction.ASC, "timestamp")));
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
   * Returns a {@link Feed} for a given username. This method simply returns a finite (currently up to 30)
   * amount of kluchs for this user, all posted before (earlier than) or after (later than) a given
   * <code>timestamp</code> (milliseconds unix time). If onlyForUser is true,
   * returns only this user's {@link Kluch}s, otherwise it includes all users they follow too.
   * Returned Kluchs are sorted by timestamp depending on the direction (descending for before and ascending for after).
   * @param username name of the user for which we want to construct the feed
   * @param timestamp unix milliseconds before (or after) which you want to construct the feed
   * @param onlyForUser flag which specifies whether Kluchs in the feed should only be for a given user (true) or for their followers too (false)
   * @param direction specifies whether you want to retrieve kluchs "after" or "before" timestamp
   * @return <code>Feed</code> of <code>Kluchs</code>
   * @throws InvalidTimestampException if timestamp is less than 0
   * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
   * @throws NullOrEmptyException if <code>username</code> is null or empty
   */
  public Feed<KluchFeedElement> constructFeed(String username, long timestamp, boolean onlyForUser, FeedDirection direction) throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {
    if (direction == FeedDirection.BEFORE) {
      return constructFeed(username, timestamp, onlyForUser);
    }
    if(direction == FeedDirection.AFTER) {
    	return constructFeedAfter(username, timestamp, onlyForUser);
    }
    return emptyFeed;
  }
  
  /**
   * Returns a {@link Feed} for a given username. This method simply returns a finite (currently 30)
   * amount of kluchs for this user, all posted before (earlier than) a given
   * <code>timestamp</code> (milliseconds unix time). If onlyForUser is true,
   * returns only this user's {@link Kluch}s, otherwise it includes all users they follow too.
   * Returned Kluchs are sorted by timestamp in descending order.
   * @param username name of the user for which we want to construct the feed
   * @param timestamp unix milliseconds before (or after) which you want to construct the feed
   * @param onlyForUser flag which specifies whether Kluchs in the feed should only be for a given user (true) or for their followers too (false)
   * @return <code>Feed</code> of <code>Kluchs</code> sorted in descending order according to timestamp
   * @throws InvalidTimestampException if timestamp is less than 0
   * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
   * @throws NullOrEmptyException if <code>username</code> is null or empty
   */
  public Feed<KluchFeedElement> constructFeed(String username, long beforeTimestamp, boolean onlyForUser) throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {    
    validateTimestamp(beforeTimestamp);
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user, onlyForUser);
    List<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampLessThan(authors, new Timestamp(beforeTimestamp), before);
    Feed<KluchFeedElement> feed = new Feed<>();
    feed.setElements(convertKluchsToFeedElements(kluchs));
    return feed;
  }
  
  /**
   * Returns a {@link Feed} for a given username. This method simply returns a finite (currently 30)
   * amount of kluchs for this user, all posted after (later than) a given
   * <code>timestamp</code> (milliseconds unix time). If onlyForUser is true,
   * returns only this user's {@link Kluch}s, otherwise it includes all users they follow too.
   * Returned Kluchs are sorted by timestamp in ascending order.
   * @param username name of the user for which we want to construct the feed
   * @param timestamp unix milliseconds before (or after) which you want to construct the feed
   * @param onlyForUser flag which specifies whether Kluchs in the feed should only be for a given user (true) or for their followers too (false)
   * @return <code>Feed</code> of <code>Kluchs</code> sorted in ascending order according to timestamp
   * @throws InvalidTimestampException if timestamp is less than 0
   * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
   * @throws NullOrEmptyException if <code>username</code> is null or empty
   */
  public Feed<KluchFeedElement> constructFeedAfter(String username, long afterTimestamp, boolean onlyForUser) throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {
    validateTimestamp(afterTimestamp);
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user, onlyForUser);
    List<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), after);
    Feed<KluchFeedElement> feed = new Feed<KluchFeedElement>();
    feed.setElements(convertKluchsToFeedElements(kluchs));
    return feed;
  }
  
  /**
   * Checks if there exist Kluchs posted after (later) given timestamp (in epoch millis).
   * @param username name of the user for which we want to poll the feed
   * @param afterTimestamp returned Kluchs were posted after this epoch millis value
   * @return true if there are Kluchs posted after given timestamp (in epoch millis)
   * @throws InvalidTimestampException if timestamp is less than 0
   * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
   * @throws NullOrEmptyException if <code>username</code> is null or empty
   */
  public boolean existsFeedAfter(String username, long afterTimestamp, boolean onlyForUser) throws InvalidTimestampException, UserDoesNotExistException, NullOrEmptyException {
    validateTimestamp(afterTimestamp);
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user, onlyForUser);
    List<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), exists);
    return !kluchs.isEmpty();
  }
  
  /**
   * Returns a {@link Feed} of {@link Kluch}s containing this hashtag. 
   * This method returns a finite (currently up to 30) number of Kluchs, all posted before (earlier than)
   * given timestamp (millis unix time).
   * Returned Kluchs are sorted by timestamp in descending order.
   * @param hashtagText does not have to contain pound character as the first character (but it can)
   * @param timestamp
   * @return <code>Feed</code> of <code>Kluchs</code>
   * @throws InvalidTimestampException if timestamp is less than 0
   * @throws NullOrEmptyException if <code>hashtagText</code> is null or empty
   */
  public Feed<KluchFeedElement> constructHashtagFeed(String hashtagText, long timestamp) throws UserDoesNotExistException, NullOrEmptyException {
    validateTimestamp(timestamp);
    Hashtag hashtag = getHashtag(hashtagText);   
    if(hashtag == null) {
      return emptyFeed;
    }
    Feed<KluchFeedElement> feed = new Feed<>();
    List<Kluch> kluchs = kluchDao.findByHashtagsInAndTimestampLessThan(hashtag, new Timestamp(timestamp), before);   
    feed.setElements(convertKluchsToFeedElements(kluchs));
    return feed;
  }
  
  /**
   * Validates username and checks if user exists. If it does, returns the User.
   * @param username
   * @return
   * @throws NullOrEmptyException if <code>username</code> is null or empty
   * @throws UserDoesNotExistException if username with given <code>username</code> doesn't exist
   */
  private User getUser(String username) throws UserDoesNotExistException, NullOrEmptyException {
    if(username == null || username.isEmpty()) {
      throw new NullOrEmptyException("Username");
    }
    User user = userDao.findOne(username);
    if(user == null) {
      throw new UserDoesNotExistException("There is no user named " + username);
    }
    return user;
  }
  
  /**
   * Checks if given hashtagText is null or empty, and attempts to get a {@link Hashtag} with given text.
   * @param hashtagText hashtagText with or without pound character as the first character
   * @return
   * @throws NullOrEmptyException if <code>hashtagText</code> is null or empty
   */
  private Hashtag getHashtag(String hashtagText) throws NullOrEmptyException {
    if(hashtagText == null || hashtagText.isEmpty()) {
      throw new NullOrEmptyException("Hashtag");
    }
    boolean hasPoundCharacter = hashtagText.startsWith("#");
    if(!hasPoundCharacter) {
      hashtagText = "#" + hashtagText;
    }
    return hashtagDao.findOne(hashtagText);
  }
  
  /**
   * Returns a feed of {@link Kluch}s with given ids.
   * @param ids a list of IDs of Kluchs you want to retrieve
   * @return a feed of kluchs, sorted in descending order according to timestamp
   */
  public Feed<KluchFeedElement> getKluchs(List<Long> ids) {
  	return new Feed<>(getKluchsAsFeedElements(ids));
  }
  
  private List<KluchFeedElement> getKluchsAsFeedElements(List<Long> ids) {
  	List<Kluch> kluchs = kluchDao.findAll((Iterable<Long>)ids);
  	return convertKluchsToFeedElements(kluchs);

  }
  
  /**
   * Validates timestamp value.
   * @param timestamp
   * @throws InvalidTimestampException if <code>timestamp</code> is negative
   */
  private void validateTimestamp(long timestamp) throws InvalidTimestampException {
    if(timestamp < 0) {
      throw new InvalidTimestampException();
    }
  }
  
  /**
   * Returns a list of authors this user would see kluchs of (so this user's kluchs and their followers' kluchs).
   * @param user
   * @param onlyForUser whether the list of authors should include only the user or also users they follow
   * @return
   */
  private List<String> getListOfAuthors(User user, boolean onlyForUser) {
    if(onlyForUser) {
      return Arrays.asList(user.getUsername());
    }
    List<String> authors = new ArrayList<>();
    authors.add(user.getUsername());
    authors.addAll(user.getFollowing());
    return authors;
  }
  
  
  private List<KluchFeedElement> convertKluchsToFeedElements(List<Kluch> kluchs) {
  	Set<String> usernames = kluchs.stream()
  			.map(k -> k.getAuthor())
  			.collect(Collectors.toSet());
  	List<User> users = userDao.findAll(usernames);
  	List<KluchFeedElement> feedElements = new ArrayList<>(kluchs.size());
  	for(Kluch k: kluchs) {
  		feedElements.add(new KluchFeedElement(k, getKluchUserView(getUserForKluch(k, users))));
  	}
  	return feedElements;
  }
  
  private User getUserForKluch(Kluch kluch, List<User> users) {
  	for(User user: users) {
  		if(user.getUsername().equals(kluch.getAuthor())) {
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
