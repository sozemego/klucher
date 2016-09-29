package com.soze.notification.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.kluch.model.Kluch;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceWithCacheTest extends TestWithMockUsers {

	@Autowired
	private UserDao userDao;

	@Autowired
	private NotificationService notificationService;

	@SuppressWarnings("rawtypes")
	@Before
	public void setUp() throws Exception {
		Class aClass = NotificationService.class;
		Field field = aClass.getDeclaredField("cachedNotificationNumber");
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
		mockUser("test", "password");
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(0));
	}

	@Test
	public void testFewNotifications() throws Exception {
		User user = mockUser("test", "password");
		user.setNotifications(4);
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(4));
	}

	@Test(expected = NullOrEmptyException.class)
	public void testProcessKluchNullKluch() throws Exception {
		notificationService.processUserMentions(null);
	}

	@Test
	public void testProcessKluchWithNoNotifications() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("some text no mention.");
		int notifications = notificationService.processUserMentions(kluch);
		assertThat(notifications, equalTo(0));
		verifyZeroInteractions(userDao);
	}

	@Test
	public void testProcessKluchWithValidNotification() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser");
		List<User> users = mockUsers(Arrays.asList("anotheruser"));
		int notifications = notificationService.processUserMentions(kluch);
		assertThat(notifications, equalTo(1));
		verify(userDao).save(users);
	}

	@Test
	public void testProcessKluchWithMultipleMentions() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @differentuser");
		List<User> users = mockUsers(Arrays.asList("anotheruser", "differentuser"));
		int notifications = notificationService.processUserMentions(kluch);
		assertThat(notifications, equalTo(2));
		verify(userDao).save(users);
	}

	@Test
	public void testProcessKluchWithMultipleOfSameMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @anotheruser");
		List<User> users = mockUsers(Arrays.asList("anotheruser"));
		int notifications = notificationService.processUserMentions(kluch);
		assertThat(notifications, equalTo(1));
		verify(userDao).save(users);
	}

	@Test
	public void testProcessKluchWithMultipleMentionsAgain() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @user_1@user @user @abc@abc @a @a");
		List<User> users = mockUsers(Arrays.asList("anotheruser", "user_1", "user", "abc", "a"));
		int notifications = notificationService.processUserMentions(kluch);
		assertThat(notifications, equalTo(5));
		verify(userDao).save(users);
	}

	@Test
	public void testProcessKluchWithNonExistentUserMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser");
		int notifications = notificationService.processUserMentions(kluch);
		assertThat(notifications, equalTo(0));
		verify(userDao, times(0)).save(anyListOf(User.class));
	}

	@Test
	public void testProcessKluchWithTwoSetsOfSameMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @anotheruser @a @a");
		List<User> users = mockUsers(Arrays.asList("anotheruser", "a"));
		int notifications = notificationService.processUserMentions(kluch);
		assertThat(notifications, equalTo(2));
		verify(userDao).save(users);
	}

	@Test(expected = NullOrEmptyException.class)
	public void testRemoveUserMentionsNullKluch() throws Exception {
		notificationService.processUserMentions(null);
	}

	@Test
	public void testRemoveUserMentions() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc");
		List<User> users = mockUsers(Arrays.asList("abc"));
		int notifications = notificationService.removeUserMentions(kluch);
		assertThat(notifications, equalTo(1));
		verify(userDao).save(users);
	}

	@Test
	public void testRemoveUserMentionsMultipleOfSameMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc @abc @abc");
		List<User> users = mockUsers(Arrays.asList("abc"));
		int notifications = notificationService.removeUserMentions(kluch);
		assertThat(notifications, equalTo(1));
		verify(userDao).save(users);
	}

	@Test
	public void testRemoveUserMentionsMultipleOfSameMentionAndDifferentMentions() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc @abc @abc @doge #love");
		List<User> users = mockUsers(Arrays.asList("abc"));
		when(userDao.findByUsernameIn(Arrays.asList("abc", "doge"))).thenReturn(users);
		int notifications = notificationService.removeUserMentions(kluch);
		assertThat(notifications, equalTo(1));
		verify(userDao).save(users);
	}

	@Test
	public void testRemoveUserMentionsValidKluchMentionedUserDoesNotExist() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc");
		int notifications = notificationService.removeUserMentions(kluch);
		assertThat(notifications, equalTo(0));
		verify(userDao, times(0)).save(anyListOf(User.class));
	}

	@Test
	public void testRemoveUserMentionsValidKluchOneExistingUserRestNonExistant() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@abc @dge @afbb");
		List<User> users = mockUsers(Arrays.asList("abc"));
		when(userDao.findByUsernameIn(Arrays.asList("abc", "dge", "afbb"))).thenReturn(users);
		int notifications = notificationService.removeUserMentions(kluch);
		assertThat(notifications, equalTo(1));
		verify(userDao).save(users);
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
		User followee = mockUser("two", "password");
		notificationService.addFollowNotification("one", "two");
		verify(userDao).save(followee);
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
	public void testRemoveFollowNotificationValid() throws Exception {
		mockUser("test");
		User followee = mockUser("follow");
		notificationService.removeFollowNotification("test", "follow");
		verify(userDao).save(followee);
	}

	@Test
	public void testReadSomeNotifications() throws Exception {
		User user = mockUser("user", "password");
		user.setNotifications(64);
		notificationService.read("user");
		verify(userDao).save(user);
		int notifications = notificationService.poll("user");
		assertThat(notifications, equalTo(0));
	}

}
