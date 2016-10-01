package com.soze.notification.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import java.lang.reflect.Field;
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
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
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
