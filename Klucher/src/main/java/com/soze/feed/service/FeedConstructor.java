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

@Service
public class FeedConstructor {

  private final KluchDao kluchDao;
  private final UserDao userDao;
  
  @Autowired
  public FeedConstructor(KluchDao kluchDao, UserDao userDao) {
    this.kluchDao = kluchDao;
    this.userDao = userDao;
  }
  
  /**
   * next cannot be null
   * @param username
   * @param next
   * @return
   */
  public Feed constructFeed(String username, long afterTimestamp) {
    User user = userDao.findOne(username);
    //get user followers
    //get a list of usernames and pass it as a parameter
    //we also need a parameter in this method, to be able to construct a feed after a certain point
    List<String> authors = new ArrayList<>();
    authors.add(username);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampLessThan(authors, new Timestamp(afterTimestamp), new PageRequest(0, 30, new Sort(new Order(Direction.DESC, "timestamp"))));
    Feed feed = new Feed();
    feed.setKluchs(kluchs);
    return feed;
  }
  
  public Feed constructFeedBefore(String username, long beforeTimestamp) {
    User user = userDao.findOne(username);
    //get user followers
    //get a list of usernames and pass it as a parameter
    //we also need a parameter in this method, to be able to construct a feed after a certain point
    List<String> authors = new ArrayList<>();
    authors.add(username);
    Timestamp stamp = new Timestamp(beforeTimestamp);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, stamp, new PageRequest(0, 3000, new Sort(new Order(Direction.ASC, "timestamp"))));
    Feed feed = new Feed();
    feed.setKluchs(kluchs);
    return feed;
  }
  
  public boolean existsFeedBefore(String username, long beforeTimestamp) {
    User user = userDao.findOne(username);
    //get user followers
    //get a list of usernames and pass it as a parameter
    //we also need a parameter in this method, to be able to construct a feed after a certain point
    List<String> authors = new ArrayList<>();
    authors.add(username);
    Page<Kluch> kluchs = kluchDao.findByAuthorInAndTimestampGreaterThan(authors, new Timestamp(beforeTimestamp), new PageRequest(0, 1));
    boolean exists = kluchs.hasContent() ? !kluchs.getContent().isEmpty() : false;
    return exists;
  }
  
}
