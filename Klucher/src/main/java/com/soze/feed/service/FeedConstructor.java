package com.soze.feed.service;

import java.sql.Timestamp;
import java.util.ArrayList;
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

  public static final int BEFORE_KLUCHS_PER_REQUEST = 30;
  public static final int AFTER_KLUCHS_PER_REQUEST = 30;
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
   * for this user, all posted before (earlier than) a given <code>afterTimestamp</code> (millis after epoch start).
   * Returned Kluchs are sorted from newest to oldest.
   * @param username cannot be null
   * @param beforeTimestamp returned Kluchs were posted before this epoch millis value
   * @return
   * @throws IllegalArgumentException if username is null or user with given name doesn't exist
   */
  public Feed constructFeed(String username, long beforeTimestamp) throws IllegalArgumentException {    
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user);
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
   * @return
   * @throws IllegalArgumentException if username is null or user with given name doesn't exist
   */
  public Feed constructFeedAfter(String username, long afterTimestamp) throws IllegalArgumentException{
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), after);
    Feed feed = new Feed();
    feed.setKluchs(kluchs);
    return feed;
  }
  
  /**
   * Checks if there exist Kluchs posted after (later) given timestamp (in epoch millis).
   * @param username cannot be null
   * @param afterTimestamp returned Kluchs were posted after this epoch millis value
   * @return true if there are Kluchs posted after given timestamp (in epoch millis)
   * @throws IllegalArgumentException if username is null or user with given name doesn't exist
   */
  public boolean existsFeedAfter(String username, long afterTimestamp) throws IllegalArgumentException {
    User user = getUser(username);
    List<String> authors = getListOfAuthors(user);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(afterTimestamp), exists);
    boolean exists = kluchs.hasContent() ? !kluchs.getContent().isEmpty() : false;
    return exists;
  }
  
  /**
   * Validates username and checks if user exists. If it does, eturns the User.
   * @param username
   * @return
   * @throws IllegalArgumentException if username is null or user with given name doesn't exist
   */
  private User getUser(String username) throws IllegalArgumentException {
    if(username == null) {
      throw new IllegalArgumentException("Username cannot be null for feed construction.");
    }
    User user = userDao.findOne(username);
    if(user == null) {
      throw new IllegalArgumentException("There is no user named " + username);
    }
    return user;
  }
  
  /**
   * Returns a list of authors this user would see kluchs of (so this user's kluchs and their followers' kluchs).
   * @param user
   * @return
   */
  private List<String> getListOfAuthors(User user) {
    List<String> authors = new ArrayList<>();
    authors.add(user.getUsername());
    return authors;
  }
  
}
