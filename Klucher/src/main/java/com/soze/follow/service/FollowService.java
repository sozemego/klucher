package com.soze.follow.service;

import java.util.Arrays;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * Service which allows users to follow other users and be followed by other
 * users.
 * 
 * @author sozek
 *
 */
@Service
public class FollowService {

  private final UserDao userDao;

  @Autowired
  public FollowService(UserDao userDao) {
    this.userDao = userDao;
  }

  /**
   * Attempts to set <code>username</code> to follow <code>follow</code>
   * and set <code>username</code> to be a follower of <code>follow</code>.
   * @param username username that is making this request
   * @param follow username to follow
   * @throws NullOrEmptyException if either <code>username</code> or <code>follow</code> are null or empty
   * @throws UserDoesNotExistException if any of the Users don't exist
   */
  public void follow(String username, String follow) throws NullOrEmptyException, UserDoesNotExistException {
    validateInput(username, follow);
    User user = userDao.findOne(username);
    if (user == null) {
      throw new UserDoesNotExistException(username);
    }
    User followUser = userDao.findOne(follow);
    if (followUser == null) {
      throw new UserDoesNotExistException(follow);
    }
    Set<String> following = user.getFollowing();
    following.add(follow);
    Set<String> followers = followUser.getFollowers();
    followers.add(username);
    userDao.save(Arrays.asList(user, followUser));
  }

  /**
   * Attempts to set <code>username</code> to unfollow <code>follow</code>
   * and set <code>username</code> to stop being a follower of <code>follow</code>.
   * @param username username that is making this request
   * @param follow username to unfollow
   * @throws NullOrEmptyException if either <code>username</code> or <code>follow</code> are null or empty
   * @throws UserDoesNotExistException if any of the Users don't exist
   */
  public void unfollow(String username, String follow) throws NullOrEmptyException, UserDoesNotExistException {
    validateInput(username, follow);
    User user = userDao.findOne(username);
    if (user == null) {
      throw new UserDoesNotExistException(username);
    }
    User followUser = userDao.findOne(follow);
    if (followUser == null) {
      throw new UserDoesNotExistException(follow);
    }
    Set<String> following = user.getFollowing();
    following.remove(follow);
    Set<String> followers = followUser.getFollowers();
    followers.remove(username);
    userDao.save(Arrays.asList(user, followUser));
  }

  private void validateInput(String username, String follow) throws NullOrEmptyException {
    if (username == null || username.isEmpty()) {
      throw new NullOrEmptyException("Username");
    }
    if (follow == null || follow.isEmpty()) {
      throw new NullOrEmptyException(
          "User you are trying to follow");
    }
  }

}
