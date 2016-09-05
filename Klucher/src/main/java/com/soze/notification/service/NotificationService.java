package com.soze.notification.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.feed.model.Feed;
import com.soze.kluch.model.Kluch;
import com.soze.notification.model.Notification;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@Service
public class NotificationService {

	private static final List<Notification> EMPTY_LIST = Arrays.asList();
	private final Pattern userMentionExtractor = Pattern.compile("(?:^|\\s)(@\\w+)");
	private final UserDao userDao;
	
	@Autowired
	public NotificationService(UserDao userDao) {
		this.userDao = userDao;
	}
	
	/**
	 * Returns a number of unread {@link Notification}s for a given <code>username</code>.
	 * @param username <code>username</code> for which we want to poll notifications
	 * @return number of unread notifications
	 * @throws NullOrEmptyException if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
	 */
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
	
	/**
	 * Finds all {@link Notification}s for a given <code>username</code>. 
	 * @param username <code>username</code> for which we want to construct the <code>feed</code>
	 * @return feed of notifications, sorted in descending order according to timestamp (latest first)
	 * @throws NullOrEmptyException if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
	 */
	public Feed<Notification> getNotifications(String username) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		List<Notification> notifications = user.getNotifications();
		return new Feed<>(notifications);
	}
	
	/**
	 * Finds notifications (@user mentions) in a given <code>kluch</code>.
	 * Mentions for users that don't exist don't produce notifications.
	 * @param kluch
	 * @return list of notifications extracted from this <code>kluch</code>. Empty if there weren't any
	 */
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
		Matcher matcher = userMentionExtractor.matcher(kluchText);
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
	
	/**
	 * Creates a notification detailing an event of one User following another user.
	 * Notification is going to be created for the user who was followed (<code>follow</code>).
	 * @param username <code>username</code> of the user which followed
	 * @param follow <code>follow</code> is the name of the user who was followed
	 * @throws NullOrEmptyException if either <code>username</code> or <code>follow</code> are null or empty
	 * @throws UserDoesNotExistException if  with given username does not exist
	 * @throws CannotDoItToYourselfException if user tried to follow himself
	 * @return a Notification for a follow event
	 */
	public Notification addFollowNotification(String username, String follow) throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
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
	
	/**
	 * Marks all notifications for this <code>username</code> as read.
	 * @param username <code>username<code> for which you want to mark all notifications as read
	 * @throws NullOrEmptyException if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
	 */
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
