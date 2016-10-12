package com.soze.notification.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class NotificationService {

	private final UserDao userDao;
	private final Map<String, Integer> cachedNotificationNumber = new ConcurrentHashMap<>();

	@Autowired
	public NotificationService(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * Returns a number of unread notifications for this user.
	 * 
	 * @param username
	 * @return number of unread notifications
	 * @throws NullOrEmptyException
	 *           if username is null or empty
	 * @throws UserDoesNotExistException
	 *           if username does not exist
	 */
	public int poll(String username) throws NullOrEmptyException, UserDoesNotExistException {
		validate(username);
		if (!cachedNotificationNumber.containsKey(username)) {
			int notifications = pollNotificationsFromDb(username);
			cachedNotificationNumber.put(username, notifications);
		}
		return cachedNotificationNumber.get(username);
	}

	private int pollNotificationsFromDb(String username) {
		User user = getUser(username);
		return user.getNotifications();
	}

	/**
	 * Adds a notification to a given user.
	 * Returns true if a notification was successfuly added to this user.
	 * @param username
	 * @return
	 * @throws NullOrEmptyException if username is either null or empty
	 * @throws UserDoesNotExistException if user with username does not exist
	 */
	public boolean addNotification(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.addNotification();
		userDao.save(user);
		return true;
	}
	
	/**
	 * Adds a notification to a given user.
	 * Returns true if a notification was successfuly added to this user.
	 * @param userId id of the user to add the notification to
	 * @return
	 * @throws NullOrEmptyException if username is either null or empty
	 * @throws UserDoesNotExistException if user with username does not exist
	 */
	public boolean addNotification(long userId) throws UserDoesNotExistException {
		User user = getUser(userId);
		user.addNotification();
		userDao.save(user);
		return true;
	}

	/**
	 * Removes a notification from a given user.
	 * Returns true if a notification was successfuly removed from this user,
	 * returns false if this user already had 0 unread notifications.
	 * @param username
	 * @return 
	 * @throws NullOrEmptyException if username is either null or empty 
	 * @throws UserDoesNotExistException if user with username does not exist
	 */
	public boolean removeNotification(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		if(user.getNotifications() > 0) {
			user.removeNotification();
			userDao.save(user);
			return true;
		}
		return false;
	}
	
	/**
	 * Removes a notification from a given user.
	 * Returns true if a notification was successfuly removed from this user,
	 * returns false if this user already had 0 unread notifications.
	 * @param userId id of the user we want to remove the notification from
	 * @return 
	 * @throws NullOrEmptyException if username is either null or empty 
	 * @throws UserDoesNotExistException if user with username does not exist
	 */
	public boolean removeNotification(long userId) throws UserDoesNotExistException {
		User user = getUser(userId);
		if(user.getNotifications() > 0) {
			user.removeNotification();
			userDao.save(user);
			return true;
		}
		return false;
	}

	/**
	 * Finds all users in a given list and adds a notification to them.
	 * 
	 * @param usernames
	 * @return number of real, existing users who had a notification added
	 */
	public int addNotifications(Collection<String> usernames) {
		List<User> realUsers = getUsers(new ArrayList<>(usernames));
		if (!realUsers.isEmpty()) {
			realUsers.forEach(u -> {
				u.addNotification();
				cachedNotificationNumber.remove(u.getUsername());
			});
			userDao.save(realUsers);
		}
		return realUsers.size();
	}

	/**
	 * Finds all users in a given list and removes a notification from them.
	 * 
	 * @param usernames
	 * @return number of real, existing users who had a notification removed
	 */
	public int removeNotifications(Collection<String> usernames) {
		List<User> realUsers = getUsers(new ArrayList<>(usernames));
		if (!realUsers.isEmpty()) {
			realUsers.forEach(u -> {
				u.removeNotification();
				cachedNotificationNumber.remove(u.getUsername());
			});
			userDao.save(realUsers);
		}
		return realUsers.size();
	}

	/**
	 * Removes all unread notifications from an user named <code>username</code>
	 * (if it exists).
	 * 
	 * @param username
	 *          name of the user we want to remove notifications from
	 * @throws NullOrEmptyException
	 *           if username is null or empty
	 * @throws UserDoesNotExistException
	 *           if user named <code>
	 */
	public void read(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.setNotifications(0);
		userDao.save(user);
		cachedNotificationNumber.remove(username);
	}

	private User getUser(String username) throws NullOrEmptyException, UserDoesNotExistException {
		validate(username);
		User user = userDao.findOne(username);
		if (user == null) {
			throw new UserDoesNotExistException(username);
		}
		return user;
	}
	
	private User getUser(long userId) throws UserDoesNotExistException {
		User user = userDao.findOne(userId);
		if (user == null) {
			throw new UserDoesNotExistException("ID " + userId);
		}
		return user;
	}

	private void validate(String username) throws NullOrEmptyException {
		if (username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
	}

	private List<User> getUsers(List<String> usernames) {
		return userDao.findByUsernameIn(usernames);
	}

}
