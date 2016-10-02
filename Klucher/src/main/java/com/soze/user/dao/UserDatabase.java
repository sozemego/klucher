package com.soze.user.dao;

import java.util.List;

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
  public User findOne(Long id) {
    return userRepository.findOne(id);
  }
  
  @Override
  public User findOne(String username) {
  	return userRepository.findByUsername(username);
  }

  @Override
  public boolean exists(Long id) {
    return userRepository.exists(id);
  }
  
  @Override
  public boolean exists(String username) {
  	User user = findOne(username);
  	if(user != null) {
  		return true;
  	}
  	return false;
  }

  @Override
  public Iterable<User> findAll() {
    return userRepository.findAll();
  }

  @Override
  public List<User> findAll(Iterable<Long> ids) {
    return userRepository.findAll(ids);
  }

  @Override
  public List<User> findByUsernameIn(Iterable<String> usernames) {
  	return userRepository.findAllByUsernameIn(usernames);
  }
  
  @Override
  public long count() {
    return userRepository.count();
  }

  @Override
  public void delete(Long id) {
    userRepository.delete(id);
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
