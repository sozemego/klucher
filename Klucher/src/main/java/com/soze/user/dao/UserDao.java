package com.soze.user.dao;

import java.util.List;

import com.soze.user.model.User;

public interface UserDao {

  public User save(User user);
  
  public Iterable<User> save(Iterable<User> users);
  
  public User findOne(Long id);
  
  public User findOne(String username);
  
  public boolean exists(Long id);
  
  public boolean exists(String username);
  
  public Iterable<User> findAll();
  
  public List<User> findAll(Iterable<Long> ids);
  
  public List<User> findByUsernameIn(Iterable<String> usernames);
  
  public long count();
  
  public void delete(Long username);
  
  public void delete(User user);
  
  public void delete(Iterable<User> users);
  
  public void deleteAll();
  
}
