package com.soze.user.dao;

import com.soze.user.model.User;

public interface UserDao {

  public User save(User user);
  
  public Iterable<User> save(Iterable<User> users);
  
  public User findOne(String username);
  
  public boolean exists(String username);
  
  public Iterable<User> findAll();
  
  public Iterable<User> findAll(Iterable<String> usernames);
  
  public long count();
  
  public void delete(String username);
  
  public void delete(User user);
  
  public void delete(Iterable<User> users);
  
  public void deleteAll();
  
  public boolean follow(String username, String follow);
  
  public boolean unfollow(String username, String follow);
  
}
