package com.soze.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.kluch.dao.KluchDao;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class UserService {

	private final UserDao userDao;
	private final KluchDao kluchDao;
	
	@Autowired
	public UserService(UserDao userDao, KluchDao kluchDao) {
		this.userDao = userDao;
		this.kluchDao = kluchDao;
	}
	
	/**
	 * Sets user with <code>username</code> to like user with <code>likedUserId</code>.
	 * Returns number of likes of <code>likedUserId</code>. If <code>username</code> already likes
	 * <code>likedUserId</code> nothing happens.
	 * @param username name of the user who likes
	 * @param likedUserId id of the user who is liked
	 * @return number of likes of user with <code>likedUserId</code>
	 * @throws NullOrEmptyException if username is null or empty
	 * @throws UserDoesNotExistException if either username or likedUserId do not exist
	 * @throws CannotDoItToYourselfException if username and likedUserId are the same user
	 */
	public int like(String username, long likedUserId) throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		User user = getUser(username);
		if(user.getId() == likedUserId) {
			throw new CannotDoItToYourselfException("" + user.getId(), "Like");
		}
		User likedUser = getUser(likedUserId);
		return like(user, likedUser);
	}
	
	/**
	 * Sets user with <code>userId</code> to like user with <code>likedUserId</code>.
	 * Returns number of likes of <code>likedUserId</code>. If <code>userId</code> already likes
	 * <code>likedUserId</code> nothing happens.
	 * @param userId id of the user who likes
	 * @param likedUserId id of the user who is liked
	 * @return number of likes of user with <code>likedUserId</code>
	 * @throws UserDoesNotExistException if either username or likedUserId do not exist
	 * @throws CannotDoItToYourselfException if username and likedUserId are the same user
	 */
	public int like(long userId, long likedUserId) throws UserDoesNotExistException, CannotDoItToYourselfException {
		if(userId == likedUserId) {
			throw new CannotDoItToYourselfException("" + userId, "Like");
		}
		User user = getUser(userId);
		User likedUser = getUser(likedUserId);
		return like(user, likedUser);
	}
	
	/**
	 * Sets user with <code>username</code> to like user with <code>likedUsername</code>.
	 * Returns number of likes of <code>likedUsername</code>. If <code>username</code> already likes
	 * <code>likedUsername</code> nothing happens.
	 * @param username name of the user who likes
	 * @param likedUsername username of the user who is liked
	 * @return number of likes of user with <code>likedUsername</code>
	 * @throws NullOrEmptyException if either username is null or empty
	 * @throws UserDoesNotExistException if either user does not exist
	 * @throws CannotDoItToYourselfException if username and likedUsername have the same user id (are the same user)
	 */
	public int like(String username, String likedUsername) throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		User user = getUser(username);
		User likedUser = getUser(likedUsername);
		if(user.getId() == likedUser.getId()) {
			throw new CannotDoItToYourselfException("" + username, "Like");
		}
		return like(user, likedUser);
	}
	
	/**
	 * Sets target to have source's id in a set of likes.
	 * @param source
	 * @param target
	 * @return number of likes of target
	 */
	private int like(User source, User target) {
		List<Long> likes = target.getLikes();
		int numberOfLikes = likes.size();
		if(!likes.contains(source.getId())) {
			likes.add(source.getId());
			userDao.save(target);
			numberOfLikes++;
		}	
		return numberOfLikes;
	}
	
	/**
	 * Sets user with <code>username</code> to unlike user with <code>unlikedUserId</code>.
	 * Returns number of likes of <code>unlikedUserId</code>. If <code>username</code> didn't already like
	 * <code>likedUserId</code> nothing happens.
	 * @param username name of the user who unlikes
	 * @param unlikedUserId id of the user who is unliked
	 * @return number of likes of user with <code>unlikedUserId</code>
	 * @throws NullOrEmptyException if username is null or empty
	 * @throws UserDoesNotExistException if either username or unlikedUserId do not exist
	 * @throws CannotDoItToYourselfException if username and unlikedUserId are the same user
	 */
	public int unlike(String username, long unlikedUserId) throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		User user = getUser(username);
		if(user.getId() == unlikedUserId) {
			throw new CannotDoItToYourselfException("" + user.getId(), "Like");
		}
		User likedUser = getUser(unlikedUserId);
		return unlike(user, likedUser);
	}
	
	/**
	 * Sets user with <code>userId</code> to unlike user with <code>unlikedUserId</code>.
	 * Returns number of likes of <code>unlikedUserId</code>. If <code>userId</code> didn't already like
	 * <code>unlikedUserId</code> nothing happens.
	 * @param userId id of the user who unlikes
	 * @param unlikedUserId id of the user who is unliked
	 * @return number of likes of user with <code>unlikedUserId</code>
	 * @throws UserDoesNotExistException if either username or unlikedUserId do not exist
	 * @throws CannotDoItToYourselfException if username and unlikedUserId are the same user
	 */
	public int unlike(long userId, long unlikedUserId) throws UserDoesNotExistException, CannotDoItToYourselfException {
		User user = getUser(userId);
		if(user.getId() == unlikedUserId) {
			throw new CannotDoItToYourselfException("" + user.getId(), "Like");
		}
		User likedUser = getUser(unlikedUserId);
		return unlike(user, likedUser);
	}
	
	/**
	 * Sets user with <code>username</code> to unlike user with <code>unlikedUsername</code>.
	 * Returns number of likes of <code>likedUsername</code>. If <code>username</code> didn't already like
	 * <code>unlikedUsername</code> nothing happens.
	 * @param username name of the user who unlikes
	 * @param unlikedUsername username of the user who is unliked
	 * @return number of likes of user with <code>unlikedUsername</code>
	 * @throws NullOrEmptyException if either username is null or empty
	 * @throws UserDoesNotExistException if either user does not exist
	 * @throws CannotDoItToYourselfException if username and unlikedUsername have the same user id (are the same user)
	 */
	public int unlike(String username, String unlikedUsername) throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		User user = getUser(username);
		User likedUser = getUser(unlikedUsername);
		if(user.getId() == likedUser.getId()) {
			throw new CannotDoItToYourselfException("" + username, "Like");
		}
		return unlike(user, likedUser);
	}	
	
	/**
	 * Removes source id from target's set of likes.
	 * @param source
	 * @param target
	 * @return number of likes of target
	 */
	private int unlike(User source, User target) {
		List<Long> likes = target.getLikes();
		int numberOfLikes = likes.size();
		if(likes.remove(source.getId())) {
			userDao.save(target);
			numberOfLikes--;
		}	
		return numberOfLikes;
	}
	
	/**
	 * Checks whether user with <code>sourceUsername</code> likes <code>targetUsername</code>.
	 * @param sourceUsername
	 * @param targetUsername
	 * @return true if source likes target
	 * @throws NullOrEmptyException
	 * @throws UserDoesNotExistException
	 */
	public boolean doesLike(String sourceUsername, String targetUsername) throws NullOrEmptyException, UserDoesNotExistException {
		User source = getUser(sourceUsername);
		User target = getUser(targetUsername);
		if(source.getId() == target.getId()) {
			return false;
		}
		return target.getLikes().contains(source.getId());
	}
	
	/**
	 * Returns a number of kluchs whose author is user with <code>username</code>.
	 * @param username
	 * @return
	 * @throws NullOrEmptyException
	 * @throws UserDoesNotExistException
	 */
	public long getNumberOfKluchs(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		return kluchDao.countByAuthorId(user.getId());
	}
	
	private User getUser(String username) throws UserDoesNotExistException {
		validateUsername(username);
		User user = userDao.findOne(username);
		if(user == null) {
			throw new UserDoesNotExistException(username);
		}
		return user;
	}
	
	private User getUser(long userId) throws UserDoesNotExistException {
		User user = userDao.findOne(userId);
		if(user == null) {
			throw new UserDoesNotExistException("" + userId);
		}
		return user;
	}
	
	private void validateUsername(String username) throws NullOrEmptyException {
		if(username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
	}
	
}
