package com.soze.notification.service;

import java.util.List;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.feed.model.Feed;
import com.soze.kluch.model.Kluch;
import com.soze.notification.model.Notification;

@Service
public interface NotificationService {

	public static final Pattern USER_MENTION_EXTRACTOR = Pattern.compile("(?:^|\\s)(@\\w+)");
	
	/**
	 * Returns a number of unread {@link Notification}s for a given <code>username</code>.
	 * @param username <code>username</code> for which we want to poll notifications
	 * @return number of unread notifications
	 * @throws NullOrEmptyException if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
	 */
	public int poll(String username) throws NullOrEmptyException, UserDoesNotExistException;
	
	/**
	 * Finds all {@link Notification}s for a given <code>username</code>. 
	 * @param username <code>username</code> for which we want to construct the <code>feed</code>
	 * @return feed of notifications, sorted in descending order according to timestamp (latest first)
	 * @throws NullOrEmptyException if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
	 */
	public Feed<Notification> getNotifications(String username) throws NullOrEmptyException, UserDoesNotExistException;
	
	/**
	 * Finds notifications (@user mentions) in a given <code>kluch</code>.
	 * Mentions for users that don't exist don't produce notifications.
	 * @param kluch
	 * @return list of notifications extracted from this <code>kluch</code>. Empty if there weren't any
	 */
	public List<Notification> processKluch(Kluch kluch);
	
	/**
	 * Creates a notification detailing an event of one User following another user.
	 * Notification is going to be created for the user who was followed (<code>follow</code>).
	 * @param username <code>username</code> of the user who followed
	 * @param follow <code>follow</code> is the name of the user who was followed
	 * @throws NullOrEmptyException if either <code>username</code> or <code>follow</code> are null or empty
	 * @throws UserDoesNotExistException if user with given username does not exist
	 * @throws CannotDoItToYourselfException if user tried to follow himself
	 * @return a Notification for a follow event
	 */
	public Notification addFollowNotification(String username, String follow) throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException;
	
	/**
	 * Removes a {@link Notification} which contains an event of <code>username</code>
	 * following a user with username <code>follow</code>.
	 * @param username <code>username</code> of the user who followed
	 * @param follow <code>follow</code> is the name of the user who was followed
	 * @return the removed Notification or null if it does not exist
	 * @throws NullOrEmptyException if either <code>username</code> or <code>follow</code> are null or empty
	 * @throws UserDoesNotExistException if user with given username does not exist
	 * @throws CannotDoItToYourselfException if user tried to follow himself
	 */
	public Notification removeFollowNotification(String username, String follow) throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException;
	
	/**
	 * Marks all notifications for this <code>username</code> as read.
	 * @param username <code>username<code> for which you want to mark all notifications as read
	 * @throws NullOrEmptyException if <code>username</code> is null or empty
	 * @throws UserDoesNotExistException if user with given <code>username</code> does not exist
	 */
	public void read(String username) throws NullOrEmptyException, UserDoesNotExistException; 
		
}
