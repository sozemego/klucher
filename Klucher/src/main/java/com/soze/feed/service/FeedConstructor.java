package com.soze.feed.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.soze.feed.model.Feed;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * A service which is responsible for creating Feeds of kluch for users.
 * @author sozek
 *
 */
@Service
public class FeedConstructor {

  private static final int BEFORE_KLUCHS_PER_REQUEST = 30;
  private static final int AFTER_KLUCHS_PER_REQUEST = 30;
  private final PageRequest before = new PageRequest(0, BEFORE_KLUCHS_PER_REQUEST, new Sort(new Order(Direction.DESC, "timestamp")));
  private final PageRequest after = new PageRequest(0, AFTER_KLUCHS_PER_REQUEST, new Sort(new Order(Direction.ASC, "timestamp")));
  private final PageRequest exists = new PageRequest(0, 1);
  private final KluchDao kluchDao;
  private final UserDao userDao;
  
  @Autowired
  public FeedConstructor(KluchDao kluchDao, UserDao userDao) {
    this.kluchDao = kluchDao;
    this.userDao = userDao;
  }
  
  /**
   * Returns a feed for a given user. This method simply returns a finite amount of kluchs
   * for this user, all posted before (earlier than) or after (later than) a given
   * <code>timestamp</code> (millis after epoch start). If onlyForUser is true,
   * returns only this user's Kluchs, otherwise it includes all users they follow too.
   * @param username
   * @param timestamp
   * @param onlyForUser
   * @param direction
   * @return
   * @throws IllegalArgumentException 
   */
  public Feed constructFeed(String username, long timestamp, boolean onlyForUser, String direction) throws IllegalArgumentException {
    if(!"after".equalsIgnoreCase(direction) && !"before".equalsIgnoreCase(direction)) {
      throw new IllegalArgumentException("Direction has to be either 'after' or 'before'.");
    }
    if (direction.equalsIgnoreCase("before")) {
      return constructFeed(username, timestamp, onlyForUser); 
    } else if (direction.equalsIgnoreCase("after")) {
      return constructFeedAfter(username, timestamp, onlyForUser);
    }
    return new Feed();
  }
  
  /**
   * Returns a feed for a given user. This method simply returns a finite amount of kluchs
   * for this user, all posted before (earlier than) a given <code>afterTimestamp</code> (millis after epoch start).
   * Returned Kluchs are sorted from newest to oldest.
   * @param username cannot be null
   * @param beforeTimestamp returned Kluchs were posted before this epoch millis value
   * @param onlyForUser true if feed should contain only this user's Kluchs
   * @return
   * @throws IllegalArgumentException if username is null, empty or user with given name doesn't exist
   */
  public Feed constructFeed(String username, long beforeTimestamp, boolean onlyForUser) throws IllegalArgumentException {    
    validateTimestamp(beforeTimestamp);
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user, onlyForUser);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampLessThan(authors, new Timestamp(beforeTimestamp), before);
    Feed feed = new Feed();
    feed.setKluchs(kluchs);
    return feed;
  }
  
  /**
   * Returns a feed for a given user. This method simply returns a finite amount of kluchs
   * for this user, all posted after (later than) given <code>afterTimestamp</code> (millis after epoch start).
   * Returned Kluchs are sorted from newest to oldest. 
   * @param username cannot be null
   * @param afterTimestamp returned Kluchs were posted after this epoch millis value
   * @param onlyForUser true if feed should contain only this user's Kluchs
   * @return
   * @throws IllegalArgumentException if username is null, empty or user with given name doesn't exist
   */
  public Feed constructFeedAfter(String username, long afterTimestamp, boolean onlyForUser) throws IllegalArgumentException {
    validateTimestamp(afterTimestamp);
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user, onlyForUser);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), after);
    Feed feed = new Feed();
    feed.setKluchs(kluchs);
    return feed;
  }
  
  /**
   * Checks if there exist Kluchs posted after (later) given timestamp (in epoch millis).
   * @param username
   * @param afterTimestamp returned Kluchs were posted after this epoch millis value
   * @return true if there are Kluchs posted after given timestamp (in epoch millis)
   * @throws IllegalArgumentException if username is null, empty or user with given name doesn't exist
   */
  public boolean existsFeedAfter(String username, long afterTimestamp, boolean onlyForUser) throws IllegalArgumentException {
    validateTimestamp(afterTimestamp);
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user, onlyForUser);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), exists);
    boolean exists = kluchs.hasContent() || !kluchs.getContent().isEmpty();
    return exists;
  }
  
  /**
   * Validates username and checks if user exists. If it does, returns the User.
   * @param username
   * @return
   * @throws IllegalArgumentException if username is null, empty or user with given name doesn't exist
   */
  private User getUser(String username) throws IllegalArgumentException {
    if(username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be null or empty for feed construction.");
    }
    User user = userDao.findOne(username);
    if(user == null) {
      throw new IllegalArgumentException("There is no user named " + username);
    }
    return user;
  }
  
  /**
   * Validates timestamp value.
   * @param timestamp
   * @throws IllegalArgumentException if timestamp is negative
   */
  private void validateTimestamp(long timestamp) throws IllegalArgumentException {
    if(timestamp < 0) {
      throw new IllegalArgumentException("Timestamp cannot be negative.");
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
  
}
