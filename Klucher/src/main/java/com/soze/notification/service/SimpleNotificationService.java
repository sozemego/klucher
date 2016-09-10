package com.soze.notification.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.feed.model.Feed;
import com.soze.kluch.model.Kluch;
import com.soze.notification.model.Notification;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Component("SimpleNotificationService")
public class SimpleNotificationService implements NotificationService {
	
	private static final List<Notification> EMPTY_LIST = Arrays.asList();
	private final UserDao userDao;
	
	@Autowired
	public SimpleNotificationService(UserDao userDao) {
		this.userDao = userDao;
	}
	
	@Override
	public int poll(String username) throws NullOrEmptyException, UserDoesNotExistException {
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
		List<Notification> notifications = user.getNotifications();
		return new Feed<>(notifications);
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
		getUser(username);
		User followUser = getUser(follow);
		if(username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "follow");
		}
		Notification n = new Notification();
		n.setFollow(username);
		followUser.getNotifications().add(n);
		userDao.save(followUser);
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
			if(username.equals(n.getFollow())) {
				toRemove = n;
				break;
			}
		}
		if(toRemove != null) {
			notifications.remove(toRemove);
		}
		userDao.save(followUser);		
		return toRemove;
	}

	@Override
	public void read(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		user.getNotifications().forEach(n -> n.setRead(true));
		userDao.save(user);
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
