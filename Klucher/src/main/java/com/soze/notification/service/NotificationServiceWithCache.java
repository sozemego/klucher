package com.soze.notification.service;

import java.util.ArrayList;
import java.util.Arrays;
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
import com.soze.notification.model.Notification;
import com.soze.notification.model.NotificationUserView;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Component("NotificationServiceWithCache")
@Primary
public class NotificationServiceWithCache implements NotificationService {
	
	private static final List<Notification> EMPTY_LIST = Arrays.asList();
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
			int notifications = getNotificationsFromDb(username);
			cachedUsers.put(username, notifications);
		}
		return cachedUsers.get(username);
	}
	
	private int getNotificationsFromDb(String username) {
		User user = getUser(username);
		List<Notification> notifications = user.getNotifications();
		List<Notification> unreadNotifications = 
				notifications
				.stream()
				.filter(e -> !e.isRead())
				.collect(Collectors.toList());
		return unreadNotifications.size();
	}

	@Override
	public Feed<Notification> getNotifications(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		List<Notification> notifications = getNotifications(user);
		return new Feed<>(notifications);
	}
	
	private List<Notification> getNotifications(User user) {
		return user.getNotifications();
	}

	@Override
	public List<Notification> processKluch(Kluch kluch) {
		Set<String> userMentions = extractUserMentions(kluch.getText());
    if(userMentions.isEmpty()) {
      return EMPTY_LIST;
    }
    
    List<User> users = getUsers(new ArrayList<>(userMentions));   
    List<Notification> notifications = getNotifications(users, kluch);
    userDao.save(users);
    users.forEach(u -> cachedUsers.remove(u.getUsername()));
    return notifications;
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
	
	/**
	 * Creates a list of {@link Notification}'s from the mentions
	 * and a {@link Kluch}.
	 * @param userMentions
	 * @param kluch
	 * @return
	 */
	private List<Notification> getNotifications(List<User> users, Kluch kluch) {
		List<Notification> notifications = users.stream()
				.map(
						u -> {
							Notification n = new Notification();
							n.setKluchId(kluch.getId());
							u.getNotifications().add(n);
							return n;
						}
					).collect(Collectors.toList());
		return notifications;
	}

	@Override
	public Notification addFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		User follower = getUser(username);
		User followUser = getUser(follow);
		if(username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "follow");
		}
		Notification n = new Notification();
		n.setNotificationUserView(new NotificationUserView(follower.getUsername(), follower.getAvatarPath()));
		followUser.getNotifications().add(n);
		userDao.save(followUser);
		cachedUsers.remove(follow);
		return n;
	}

	@Override
	public Notification removeFollowNotification(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		getUser(username);
		User followUser = getUser(follow);
		if(username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "follow");
		}
		List<Notification> notifications = followUser.getNotifications();
		Notification toRemove = null;
		for(Notification n: notifications) {
			if(n.getNotificationUserView() != null) {
				if(username.equals(n.getNotificationUserView().getUsername())) {
					toRemove = n;
					break;
				}
			}
		}
		if(toRemove != null) {
			notifications.remove(toRemove);
		}
		userDao.save(followUser);
		cachedUsers.remove(follow);
		return toRemove;
	}

	@Override
	public void read(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.getNotifications().forEach(n -> n.setRead(true));
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
