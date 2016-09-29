package com.soze.notification.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.kluch.model.Kluch;
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
	 * Finds user mentions (@) in a given <code>kluch</code>. Notification is
	 * added to every mentioned, existing user.
	 * 
	 * @param kluch
	 *          kluch from which we want to extract user mentions (@)
	 * @return int number of real users who were mentioned
	 * @throws NullOrEmptyException
	 *           if kluch is null
	 */
	public int processUserMentions(Kluch kluch) throws NullOrEmptyException {
		if (kluch == null) {
			throw new NullOrEmptyException("Kluch");
		}
		List<String> userMentions = kluch.getMentions();
		if (userMentions.isEmpty()) {
			return 0;
		}

		List<User> realUsers = getUsers(new ArrayList<>(userMentions));
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
	 * Extracts user mentions (@) from this Kluch and for each real user, removes
	 * a notification.
	 * 
	 * @param kluch
	 * @return int number of real users who had a notification removed, even if their unread notification count was already 0
	 * @throws NullOrEmptyException
	 *           if kluch is null
	 */
	public int removeUserMentions(Kluch kluch) throws NullOrEmptyException {
		if (kluch == null) {
			throw new NullOrEmptyException("Kluch");
		}
		List<String> userMentions = kluch.getMentions();
		if (userMentions.isEmpty()) {
			return 0;
		}
		List<User> realUsers = getUsers(new ArrayList<>(userMentions));
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
	 * Adds a notification to a user named <code>follow</code> (if it exists).
	 * 
	 * @param username
	 *          name of the user who followed
	 * @param follow
	 *          name of the user who was followed
	 * @throws NullOrEmptyException
	 *           if either username or follow are null or empty
	 * @throws UserDoesNotExistException
	 *           if either username or follow don't exist
	 * @throws CannotDoItToYourselfException
	 *           if username and follow are equal
	 */
	public void addFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		getUser(username);
		User followUser = getUser(follow);
		if (username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "follow");
		}
		followUser.addNotification();
		userDao.save(followUser);
		cachedNotificationNumber.remove(follow);
	}

	/**
	 * remove a notification to a user named <code>follow</code> (if it exists).
	 * 
	 * @param username
	 *          name of the user who followed
	 * @param follow
	 *          name of the user who was followed
	 * @throws NullOrEmptyException
	 *           if either username or follow are null or empty
	 * @throws UserDoesNotExistException
	 *           if either username or follow don't exist
	 * @throws CannotDoItToYourselfException
	 *           if username and follow are equal
	 */
	public void removeFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		getUser(username);
		User followUser = getUser(follow);
		if (username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "unfollow");
		}
		followUser.removeNotification();
		userDao.save(followUser);
		cachedNotificationNumber.remove(follow);
	}

	/**
	 * Removes all unread notifications from an user named <code>username</code>
	 * (if it exists).
	 * @param username name of the user we want to remove notifications from
	 * @throws NullOrEmptyException if username is null or empty
	 * @throws UserDoesNotExistException if user named <code>
	 */
	public void read(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.setNotifications(0);
		userDao.save(user);
		cachedNotificationNumber.remove(username);
	}

	private void validate(String username) throws NullOrEmptyException {
		if (username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
	}

	private User getUser(String username) throws NullOrEmptyException, UserDoesNotExistException {
		validate(username);
		User user = userDao.findOne(username);
		if (user == null) {
			throw new UserDoesNotExistException(username);
		}
		return user;
	}

	private List<User> getUsers(List<String> usernames) {
		return userDao.findByUsernameIn(usernames);
	}

}
