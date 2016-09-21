package com.soze.notification.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.feed.model.Feed;
import com.soze.kluch.model.Kluch;
import com.soze.notification.model.FollowNotification;
import com.soze.notification.model.MentionNotification;
import com.soze.notification.model.Notification;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Component("NotificationServiceWithCache")
@Primary
public class NotificationServiceWithCache implements NotificationService {
	
	private final UserDao userDao;
	private final Map<String, Integer> cachedUsers = new ConcurrentHashMap<>();
	
	@Autowired
	public NotificationServiceWithCache(UserDao userDao) {
		this.userDao = userDao;
	}

	@Override
	public int poll(String username) throws NullOrEmptyException, UserDoesNotExistException {
		validate(username);
		if(!cachedUsers.containsKey(username)) {
			int notifications = pollNotificationsFromDb(username);
			cachedUsers.put(username, notifications);
		}
		return cachedUsers.get(username);
	}
	
	private int pollNotificationsFromDb(String username) {
		User user = getUser(username);
		List<MentionNotification> mentionNotifications = user.getMentionNotifications();
		List<FollowNotification> followNotifications = user.getFollowNotifications();
		int unreadMentionNotifications = mentionNotifications.stream()
				.mapToInt(n -> n.isNoticed() ? 0 : 1).sum();
		int unreadFollowNotifications = followNotifications.stream()
				.mapToInt(n -> n.isNoticed() ? 0 : 1).sum();
		return unreadMentionNotifications + unreadFollowNotifications;
	}

	@Override
	public Feed<Notification> getNotifications(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		Set<Notification> notifications = getNotifications(user);
		return new Feed<>(notifications);
	}
	
	private Set<Notification> getNotifications(User user) {
		Set<Notification> notifications = new HashSet<>();
		notifications.addAll(user.getFollowNotifications());
		notifications.addAll(user.getMentionNotifications());
		return notifications;
	}

	@Override
	public Notification processUserMentions(Kluch kluch) {
		if(kluch == null) {
			throw new NullOrEmptyException("Kluch");
		}
		Set<String> userMentions = extractUserMentions(kluch.getText());
    if(userMentions.isEmpty()) {
      return null;
    }
    
    MentionNotification notification = getMentionNotification(kluch);
    List<User> realUsers = getUsers(new ArrayList<>(userMentions));
    if(!realUsers.isEmpty()) {
    	realUsers.forEach(u ->  {
    		u.getMentionNotifications().add(notification);
    		cachedUsers.remove(u.getUsername());
    	});
    	userDao.save(realUsers);
    }
    return notification;
	}
	
	private Set<String> extractUserMentions(String kluchText) {
		Matcher matcher = USER_MENTION_EXTRACTOR.matcher(kluchText);
    Set<String> mentions = new HashSet<>();     
    while(matcher.find()) {
      String mention = matcher.group(1);
      // at sign is used to extract a mention, but we need only the username
      mentions.add(mention.substring(1));
    }
    return mentions;
	}
	
	@Override
	public Notification removeUserMentions(Kluch kluch) {
		if(kluch == null) {
			throw new NullOrEmptyException("Kluch");
		}
		Set<String> userMentions = extractUserMentions(kluch.getText());
    if(userMentions.isEmpty()) {
      return null;
    }
    Notification notification = getMentionNotification(kluch);
    List<User> realUsers = getUsers(new ArrayList<>(userMentions));
    List<User> modifiedUsers = removeNotifications(realUsers, notification);
    if(!modifiedUsers.isEmpty()) {
    	userDao.save(modifiedUsers);
    	modifiedUsers.forEach(u -> cachedUsers.remove(u.getUsername()));
    	return notification;
    }
    return null;
	}
	
	private MentionNotification getMentionNotification(Kluch kluch) {
		MentionNotification n = new MentionNotification();
		n.setKluchId(kluch.getId());
		return n;
	}
	
	/**
	 * Removes given notification from each user in the list <code>users</code>.
	 * Returns a list of users who had a notification removed.
	 * @param users
	 * @param notification
	 * @return List of users that have been modified
	 */
	private List<User> removeNotifications(List<User> users, Notification notification) {
		List<User> modifiedUsers = users.stream()
			.filter(u -> { 
				return
						(u.getMentionNotifications().remove(notification) || u.getFollowNotifications().remove(notification));
			})
			.collect(Collectors.toList());
		return modifiedUsers;
	}

	@Override
	public Notification addFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		User follower = getUser(username);
		User followUser = getUser(follow);
		if(username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "follow");
		}
		FollowNotification n = getFollowNotification(follower);
		followUser.getFollowNotifications().add(n);
		userDao.save(followUser);
		cachedUsers.remove(follow);
		return n;
	}

	private FollowNotification getFollowNotification(User follower) {
		FollowNotification n = new FollowNotification();
		n.setUsername(follower.getUsername());
		n.setAvatarPath(follower.getAvatarPath());
		return n;
	}

	@Override
	public Notification removeFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		getUser(username);
		User followUser = getUser(follow);
		if(username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "unfollow");
		}
		List<FollowNotification> notifications = followUser.getFollowNotifications();
		Notification toRemove = new FollowNotification(username);
		boolean removed = notifications.remove(toRemove);
		if (removed) {
			userDao.save(followUser);
			cachedUsers.remove(follow);
			return toRemove;
		}
		return null;
	}

	@Override
	public void read(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.getFollowNotifications().forEach(n -> n.setNoticed(true));
		user.getMentionNotifications().forEach(n -> n.setNoticed(true));
		userDao.save(user);
		cachedUsers.remove(username);
	}
	
	private void validate(String username) throws NullOrEmptyException {
		if(username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
	}
	
	private User getUser(String username) throws NullOrEmptyException, UserDoesNotExistException {
		validate(username);
		User user = userDao.findOne(username);
		if(user == null) {
			throw new UserDoesNotExistException(username);
		}
		return user;
	}
	
	private List<User> getUsers(List<String> usernames) {
		return userDao.findAll(usernames);
	}

}
