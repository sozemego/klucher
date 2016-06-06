package com.soze.user.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.user.model.User;
import com.soze.user.repository.UserRepository;

@Service
public class UserDatabase implements UserDao {

  private final UserRepository userRepository;
  
  @Autowired
  public UserDatabase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  @Override
  public User save(User user) {
    return userRepository.save(user);
  }

  @Override
  public Iterable<User> save(Iterable<User> users) {
    return userRepository.save(users);
  }

  @Override
  public User findOne(String username) {
    return userRepository.findOne(username);
  }

  @Override
  public boolean exists(String username) {
    return userRepository.exists(username);
  }

  @Override
  public Iterable<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public Iterable<User> findAll(Iterable<String> usernames) {
    return userRepository.findAll(usernames);
  }

  @Override
  public long count() {
    return userRepository.count();
  }

  @Override
  public void delete(String username) {
    userRepository.delete(username);
  }

  @Override
  public void delete(User user) {
    userRepository.delete(user);
  }

  @Override
  public void delete(Iterable<User> users) {
    userRepository.delete(users);
  }

  @Override
  public void deleteAll() {
    userRepository.deleteAll();
  }
  
}
