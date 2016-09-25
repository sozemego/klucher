package com.soze.notification.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.kluch.model.Kluch;
import com.soze.notification.model.FollowNotification;
import com.soze.notification.model.MentionNotification;
import com.soze.notification.model.Notification;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class NotificationService {

	public static final Pattern USER_MENTION_EXTRACTOR = Pattern.compile("(?:^|\\s)(@\\w+)");
	private final UserDao userDao;
	private final Map<String, Integer> cachedNotificationNumber = new ConcurrentHashMap<>();

	@Autowired
	public NotificationService(UserDao userDao) {
		this.userDao = userDao;
	}

	/**
	 * Returns a number of unread {@link Notification}s for a given
	 * <code>username</code>.
	 * 
	 * @param username
	 *          <code>username</code> for which we want to poll notifications
	 * @return number of unread notifications
	 * @throws NullOrEmptyException
	 *           if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
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
		List<MentionNotification> mentionNotifications = user.getMentionNotifications();
		List<FollowNotification> followNotifications = user.getFollowNotifications();
		int unreadMentionNotifications = mentionNotifications.stream().mapToInt(n -> n.isNoticed() ? 0 : 1).sum();
		int unreadFollowNotifications = followNotifications.stream().mapToInt(n -> n.isNoticed() ? 0 : 1).sum();
		return unreadMentionNotifications + unreadFollowNotifications;
	}

	/**
	 * Finds user mentions (@) in a given <code>kluch</code>. Notification is
	 * saved in all existing user objects, mentions for users that don't exist
	 * don't produce notifications.
	 * 
	 * @param kluch
	 *          kluch from which we want to extract user mentions (@)
	 * @return Notification for a given Kluch or null if kluch has no user
	 *         mentions
	 * @throws NullOrEmptyException
	 *           if kluch is null
	 */
	public Notification processUserMentions(Kluch kluch) {
		if (kluch == null) {
			throw new NullOrEmptyException("Kluch");
		}
		Set<String> userMentions = extractUserMentions(kluch.getText());
		if (userMentions.isEmpty()) {
			return null;
		}

		MentionNotification notification = getMentionNotification(kluch);
		List<User> realUsers = getUsers(new ArrayList<>(userMentions));
		if (!realUsers.isEmpty()) {
			realUsers.forEach(u -> {
				u.getMentionNotifications().add(notification);
				cachedNotificationNumber.remove(u.getUsername());
			});
			userDao.save(realUsers);
		}
		return notification;
	}

	private Set<String> extractUserMentions(String kluchText) {
		Matcher matcher = USER_MENTION_EXTRACTOR.matcher(kluchText);
		Set<String> mentions = new HashSet<>();
		while (matcher.find()) {
			String mention = matcher.group(1);
			// at sign is used to extract a mention, but we need only the username
			mentions.add(mention.substring(1));
		}
		return mentions;
	}

	/**
	 * Extracts user mentions (@) from a given <code>kluch</code>. All users which
	 * exist and are mentioned have a notification for this kluch removed.
	 * 
	 * @param kluch
	 *          that was deleted
	 * @return notification which was removed, or null if there were none
	 */
	public Notification removeUserMentions(Kluch kluch) {
		if (kluch == null) {
			throw new NullOrEmptyException("Kluch");
		}
		Set<String> userMentions = extractUserMentions(kluch.getText());
		if (userMentions.isEmpty()) {
			return null;
		}
		Notification notification = getMentionNotification(kluch);
		List<User> realUsers = getUsers(new ArrayList<>(userMentions));
		List<User> modifiedUsers = removeNotifications(realUsers, notification);
		if (!modifiedUsers.isEmpty()) {
			userDao.save(modifiedUsers);
			modifiedUsers.forEach(u -> cachedNotificationNumber.remove(u.getUsername()));
			return notification;
		}
		return null;
	}

	private MentionNotification getMentionNotification(Kluch kluch) {
		MentionNotification n = new MentionNotification();
		n.setKluchId(kluch.getId());
		n.setTimestamp(kluch.getTimestamp().getTime());
		return n;
	}

	/**
	 * Removes given notification from each user in the list <code>users</code>.
	 * Returns a list of users who had a notification removed.
	 * 
	 * @param users
	 * @param notification
	 * @return List of users that have been modified
	 */
	private List<User> removeNotifications(List<User> users, Notification notification) {
		List<User> modifiedUsers = users.stream().filter(u -> {
			return (u.getMentionNotifications().remove(notification) || u.getFollowNotifications().remove(notification));
		}).collect(Collectors.toList());
		return modifiedUsers;
	}

	/**
	 * Creates a notification detailing an event of one User following another
	 * user. Notification is going to be created for the user who was followed
	 * (<code>follow</code>).
	 * 
	 * @param username
	 *          <code>username</code> of the user who followed
	 * @param follow
	 *          <code>follow</code> is the name of the user who was followed
	 * @throws NullOrEmptyException
	 *           if either <code>username</code> or <code>follow</code> are null
	 *           or empty
	 * @throws UserDoesNotExistException
	 *           if user with given username does not exist
	 * @throws CannotDoItToYourselfException
	 *           if user tried to follow himself
	 * @return a Notification for a follow event
	 */
	public Notification addFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		User follower = getUser(username);
		User followUser = getUser(follow);
		if (username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "follow");
		}
		FollowNotification n = getFollowNotification(follower);
		followUser.getFollowNotifications().add(n);
		userDao.save(followUser);
		cachedNotificationNumber.remove(follow);
		return n;
	}

	private FollowNotification getFollowNotification(User follower) {
		FollowNotification n = new FollowNotification();
		n.setUsername(follower.getUsername());
		n.setAvatarPath(follower.getAvatarPath());
		n.setTimestamp(System.currentTimeMillis());
		return n;
	}

	/**
	 * Removes a {@link Notification} which contains an event of
	 * <code>username</code> following a user with username <code>follow</code>.
	 * 
	 * @param username
	 *          <code>username</code> of the user who followed
	 * @param follow
	 *          <code>follow</code> is the name of the user who was followed
	 * @return the removed Notification or null if it does not exist
	 * @throws NullOrEmptyException
	 *           if either <code>username</code> or <code>follow</code> are null
	 *           or empty
	 * @throws UserDoesNotExistException
	 *           if user with given username does not exist
	 * @throws CannotDoItToYourselfException
	 *           if user tried to follow himself
	 */
	public Notification removeFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		getUser(username);
		User followUser = getUser(follow);
		if (username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "unfollow");
		}
		List<FollowNotification> notifications = followUser.getFollowNotifications();
		Notification toRemove = new FollowNotification(username);
		boolean removed = notifications.remove(toRemove);
		if (removed) {
			userDao.save(followUser);
			cachedNotificationNumber.remove(follow);
			return toRemove;
		}
		return null;
	}

	/**
	 * Marks all notifications for this <code>username</code> as read.
	 * 
	 * @param username
	 *          <code>username<code> for which you want to mark all notifications as read
	 * @throws NullOrEmptyException if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException
	 *           if user with given <code>username</code> does not exist
	 */
	public void read(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.getFollowNotifications().forEach(n -> n.setNoticed(true));
		user.getMentionNotifications().forEach(n -> n.setNoticed(true));
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
		return userDao.findAll(usernames);
	}

}
