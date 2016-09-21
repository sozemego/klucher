package com.soze.notification.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.soze.TestWithMockUsers;
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

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceWithCacheTest extends TestWithMockUsers {
	
	@Autowired
	private UserDao userDao;
	
	@Autowired
	@Qualifier("NotificationServiceWithCache")
	private NotificationService notificationService;
	
	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		Class aClass = NotificationServiceWithCache.class;
		Field field = aClass.getDeclaredField("cachedUsers");
		field.setAccessible(true);
		Map<String, Integer> toReplace = new ConcurrentHashMap<String, Integer>();
		field.set(notificationService, toReplace);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testNullUsernamePoll() throws Exception {
		notificationService.poll(null);
	}

	@Test(expected = NullOrEmptyException.class)
	public void testEmptyUsernamePoll() throws Exception {
		notificationService.poll("");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testUserDoesNotExist() throws Exception {
		notificationService.poll("test");
	}
	
	@Test
	public void testNoNotifications() throws Exception {
		User user = mockUser("test", "password");
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(0));
	}
	
	@Test
	public void testAFewUnreadNotifications() throws Exception {
		User user = mockUser("test", "password");
		user.setFollowNotifications(Arrays.asList(new FollowNotification("user1"), new FollowNotification("user2")));
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(2));
	}
	
	@Test
	public void testAFewReadAndUnreadNotifications() throws Exception {
		User user = mockUser("test", "password");
		List<FollowNotification> notifications = new ArrayList<>();
		// notifications start as unread by default
		notifications.add(new FollowNotification("user1"));
		notifications.add(new FollowNotification("user2"));
		FollowNotification readNotification = new FollowNotification("user3");
		readNotification.setNoticed(true);
		notifications.add(readNotification);
		user.setFollowNotifications(notifications);
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(2));
	}
	
	@Test
	public void testAllUnreadNotifications() throws Exception {
		User user = mockUser("test", "password");
		List<FollowNotification> notifications = new ArrayList<>();
		// notifications start as unread by default
		notifications.add(new FollowNotification("user1"));
		notifications.add(new FollowNotification("user2"));
		notifications.add(new FollowNotification("user3"));
		notifications.add(new FollowNotification("user4"));
		user.setFollowNotifications(notifications);
		
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(4));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testNullUsernameGetNotifications() throws Exception {
		notificationService.getNotifications(null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testEmptyUsernameGetNotifications() throws Exception {
		notificationService.getNotifications("");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testUserDoesNotExistGetNotifications() throws Exception {
		notificationService.getNotifications("doesNotExist");
	}
	
	@Test
	public void testGetNoNotifications() throws Exception {
		mockUser("test", "password");
		Feed<Notification> notifications = notificationService.getNotifications("test");
		assertThat(notifications.getElements().size(), equalTo(0));
	}
	
	@Test
	public void testAFewNotifications() throws Exception {
		User user = mockUser("test", "password");
		user.getFollowNotifications().add(new FollowNotification("user1"));
		user.getFollowNotifications().add(new FollowNotification("user2"));
		Feed<Notification> notifications = notificationService.getNotifications("test");
		assertThat(notifications.getElements().size(), equalTo(2));
	}
	
	@Test
	public void testGetManyNotifications() throws Exception {
		User user = mockUser("test", "password");
		user.getFollowNotifications().add(new FollowNotification("user1"));
		user.getFollowNotifications().add(new FollowNotification("user2"));
		user.getFollowNotifications().add(new FollowNotification("user3"));
		user.getFollowNotifications().add(new FollowNotification("user4"));
		user.getMentionNotifications().add(new MentionNotification(1L));
		user.getMentionNotifications().add(new MentionNotification(2L));
		user.getMentionNotifications().add(new MentionNotification(3L));
		user.getMentionNotifications().add(new MentionNotification(4L));
		Feed<Notification> notifications = notificationService.getNotifications("test");
		assertThat(notifications.getElements().size(), equalTo(8));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testProcessKluchNullKluch() throws Exception {
		notificationService.processUserMentions(null);
	}
	
	@Test
	public void testProcessKluchWithNoNotifications() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("some text no mention.");
		Notification notification = notificationService.processUserMentions(kluch);
		assertThat(notification, nullValue());
		verifyZeroInteractions(userDao);
	}
	
	@Test
	public void testProcessKluchWithValidNotification() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser");
		when(kluch.getId()).thenReturn(1L);
		List<User> users = mockUsers(Arrays.asList("anotheruser"));
		MentionNotification notification = (MentionNotification) notificationService.processUserMentions(kluch);
		assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		verify(userDao).save(users);
	}
	
	@Test
	public void testProcessKluchWithMultipleMentions() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @differentuser");
		when(kluch.getId()).thenReturn(1L);
		List<User> users = mockUsers(Arrays.asList("anotheruser", "differentuser"));
		MentionNotification notification = (MentionNotification) notificationService.processUserMentions(kluch);
		assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		verify(userDao).save(users);
	}
	
	@Test
	public void testProcessKluchWithMultipleMentionsAgain() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @user_1@user @user @abc@abc @a @a");
		when(kluch.getId()).thenReturn(1L);
		List<User> users = mockUsers(Arrays.asList("anotheruser", "user_1", "user", "abc", "a"));
		MentionNotification notification = (MentionNotification) notificationService.processUserMentions(kluch);
		assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		verify(userDao).save(users);
	}
	
	@Test
	public void testProcessKluchWithNonExistentUserMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser");
		when(kluch.getId()).thenReturn(1L);
		MentionNotification notification = (MentionNotification) notificationService.processUserMentions(kluch);
		assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		verify(userDao, times(0)).save(anyListOf(User.class));
	}
	
	@Test
	public void testProcessKluchWithMultipleOfSameMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @anotheruser");
		when(kluch.getId()).thenReturn(1L);
		// notification service retrieves a list of users in
		// kluch text in one DB call, so we want to mock
		List<User> users = mockUsers(Arrays.asList("anotheruser"));
		MentionNotification notification = (MentionNotification) notificationService.processUserMentions(kluch);
		assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		verify(userDao).save(users);
	}
	
	@Test
	public void testProcessKluchWithTwoSetsOfSameMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @anotheruser @a @a");
		when(kluch.getId()).thenReturn(1L);
		List<User> users = mockUsers(Arrays.asList("anotheruser", "a"));
		MentionNotification notification = (MentionNotification) notificationService.processUserMentions(kluch);
		assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		verify(userDao).save(users);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testRemoveUserMentionsNullKluch() throws Exception {
		notificationService.processUserMentions(null);
	}

	@Test
	public void testRemoveUserMentionsUserNeverMentioned() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc");
		when(kluch.getId()).thenReturn(1L);
		List<User> users = mockUsers(Arrays.asList("abc"));
		MentionNotification notification = (MentionNotification) notificationService.removeUserMentions(kluch);
	  assertThat(notification, nullValue());
	}
	
	@Test
	public void testRemoveUserMentionsValidKluchValidUsers() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc");
		when(kluch.getId()).thenReturn(1L);
		List<User> users = mockUsers(Arrays.asList("abc"));
		MentionNotification n = new MentionNotification();
		n.setKluchId(1L);
		users.forEach(u -> u.getMentionNotifications().add(n));
		MentionNotification notification = (MentionNotification) notificationService.removeUserMentions(kluch);
	  assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		verify(userDao).save(users);
	}
	
	@Test
	public void testRemoveUserMentionsValidKluchMentionedUserDoesNotExist() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc");
		when(kluch.getId()).thenReturn(1L);
		mockUsers(Arrays.asList("user"));
		MentionNotification notification = (MentionNotification) notificationService.removeUserMentions(kluch);
	  assertThat(notification, nullValue());
		verify(userDao, times(0)).save(anyListOf(User.class));
	}
	
	@Test
	public void testRemoveUserMentionsValidKluchOneExistingUserRestNonExistant() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc @dge @afbb");
		when(kluch.getId()).thenReturn(1L);
		List<User> users = mockUsers(Arrays.asList("abc", "dge", "afbb"));
		MentionNotification n = new MentionNotification();
		n.setKluchId(1L);
		users.get(0).getMentionNotifications().add(n);
		MentionNotification notification = (MentionNotification) notificationService.removeUserMentions(kluch);
	  assertThat(notification, notNullValue());
		assertThat(notification.getKluchId(), equalTo(1L));
		List<User> validUsers = mockUsers(Arrays.asList("abc"));
		verify(userDao).save(validUsers);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testAddFollowNotificationNullUsername() throws Exception {
		notificationService.addFollowNotification(null, "valid");
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testAddFollowNotificationEmptyUsername() throws Exception {
		notificationService.addFollowNotification("", "valid");
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testAddFollowNotificationNullFollow() throws Exception {
		mockUser("valid");
		notificationService.addFollowNotification("valid", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testAddFollowNotificationEmptyFollow() throws Exception {
		mockUser("valid");
		notificationService.addFollowNotification("valid", "");
	}
	
	@Test(expected = CannotDoItToYourselfException.class)
	public void testAddFollowNotificationFollowYourself() throws Exception {
		mockUser("valid", "password");
		notificationService.addFollowNotification("valid", "valid");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testAddFollowNotificationFollowYourselfUserDoesNotExist() throws Exception {
		notificationService.addFollowNotification("valid", "valid");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testAddFollowNotificationUserDoesNotExist() throws Exception {
		mockUser("two", "password");
		notificationService.addFollowNotification("one", "two");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testAddFollowNotificationFollowDoesNotExist() throws Exception {
		mockUser("one", "password");
		notificationService.addFollowNotification("one", "two");
	}
	
	@Test
	public void testAddFollowNotificationValid() throws Exception {
		mockUser("one", "password");
		mockUser("two", "password");
		FollowNotification notification = (FollowNotification) notificationService.addFollowNotification("one", "two");
		assertThat(notification.getUsername(), equalTo("one"));
	}
	
	@Test
	public void testAddFollowNotificationMultipleValid() throws Exception {
		mockUser("one", "password");
		mockUser("two", "password");
		FollowNotification notification = (FollowNotification) notificationService.addFollowNotification("one", "two");
		assertThat(notification.getUsername(), equalTo("one"));
		mockUser("three", "password");
		notification = (FollowNotification) notificationService.addFollowNotification("one", "three");
		assertThat(notification.getUsername(), equalTo("one"));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testReadNotificationsNullUser() throws Exception {
		notificationService.read(null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testReadNotificationsEmptyUser() throws Exception {
		notificationService.read("");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testReadNotificationsUserDoesNotExist() throws Exception {
		notificationService.read("valid");
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testRemoveFollowNotificationUsernameNull() throws Exception {
		notificationService.removeFollowNotification(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testRemoveFollowNotificationUsernameEmpty() throws Exception {
		notificationService.removeFollowNotification("", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testRemoveFollowNotificationFollowNull() throws Exception {
		mockUser("test");
		notificationService.removeFollowNotification("test", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testRemoveFollowNotificationFollowEmpty() throws Exception {
		mockUser("test");
		notificationService.removeFollowNotification("test", "");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testRemoveFollowNotificationUsernameDoesNotExist() throws Exception {
		notificationService.removeFollowNotification("test", "");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testRemoveFollowNotificationFollowDoesNotExist() throws Exception {
		mockUser("test");
		notificationService.removeFollowNotification("test", "follow");
		
	}
	
	@Test(expected = CannotDoItToYourselfException.class)
	public void testRemoveFollowNotificationUsernameAndFollowEqual() throws Exception {
		mockUser("test");
		notificationService.removeFollowNotification("test", "test");
	}
	
	@Test
	public void testRemoveFollowNotificationDoesNotExist() throws Exception {
		mockUser("test");
		mockUser("follow");
		Notification n = notificationService.removeFollowNotification("test", "follow");
		assertThat(n, equalTo(null));
	}
	
	@Test
	public void testRemoveFollowNotificationValid() throws Exception {
		mockUser("test");
		User follow = mockUser("follow");
		FollowNotification notification = new FollowNotification();
		notification.setUsername("test");
		follow.getFollowNotifications().add(notification);
		FollowNotification n = (FollowNotification) notificationService.removeFollowNotification("test", "follow");
		assertThat(n, notNullValue());
		assertThat(n.getUsername(), equalTo("test"));
	}
	
	@Test
	public void testRemoveFollowNotificationTwiceValid() throws Exception {
		mockUser("test");
		User follow = mockUser("follow");
		FollowNotification notification = new FollowNotification();
		notification.setUsername("test");
		follow.getFollowNotifications().add(notification);
		FollowNotification n = (FollowNotification) notificationService.removeFollowNotification("test", "follow");
		assertThat(n, notNullValue());
		assertThat(n.getUsername(), equalTo("test"));
		n = (FollowNotification) notificationService.removeFollowNotification("test", "follow");
		assertThat(n, nullValue());
	}
	
	@Test
	public void testNumberOfNotificationsChangesWhenFollowNotificationRemoved() throws Exception {
		mockUser("test");
		User follow = mockUser("follow");
		FollowNotification notification = new FollowNotification();
		notification.setUsername("test");
		follow.getFollowNotifications().add(notification);
		int notifications = notificationService.poll("follow");
		assertThat(notifications, equalTo(1));
		FollowNotification n = (FollowNotification) notificationService.removeFollowNotification("test", "follow");
		assertThat(n, notNullValue());
		assertThat(n.getUsername(), equalTo("test"));
		notifications = notificationService.poll("follow");
		assertThat(notifications, equalTo(0));
	}
	
	@Test
	public void testReadSomeNotifications() throws Exception {
		User user = mockUser("user", "password");
		user.getFollowNotifications().add(new FollowNotification());
		user.getFollowNotifications().add(new FollowNotification());
		user.getMentionNotifications().add(new MentionNotification());
		user.getMentionNotifications().add(new MentionNotification());
		notificationService.read("user");
		for(FollowNotification n: user.getFollowNotifications()) {
			if(!n.isNoticed()) {
				fail("All notifications should be read.");
			}
		}
		for(MentionNotification n: user.getMentionNotifications()) {
			if(!n.isNoticed()) {
				fail("All notifications should be read.");
			}
		}
	}

}
