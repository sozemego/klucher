package com.soze.notification.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.feed.model.Feed;
import com.soze.kluch.model.Kluch;
import com.soze.notification.model.Notification;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceTest extends TestWithMockUsers {

	@Autowired
	private NotificationService notificationService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
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
		user.setNotifications(Arrays.asList());
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(0));
	}
	
	@Test
	public void testAFewUnreadNotifications() throws Exception {
		User user = mockUser("test", "password");
		user.setNotifications(Arrays.asList(new Notification(), new Notification()));
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(2));
	}
	
	@Test
	public void testAFewReadAndUnreadNotifications() throws Exception {
		User user = mockUser("test", "password");
		List<Notification> notifications = new ArrayList<>();
		// notifications start as unread by default
		notifications.add(new Notification());
		notifications.add(new Notification());
		Notification readNotification = new Notification();
		readNotification.setRead(true);
		notifications.add(readNotification);
		user.setNotifications(notifications);
		int unreadNotifications = notificationService.poll("test");
		assertThat(unreadNotifications, equalTo(2));
	}
	
	@Test
	public void testAllUnreadNotifications() throws Exception {
		User user = mockUser("test", "password");
		List<Notification> notifications = new ArrayList<>();
		// notifications start as unread by default
		notifications.add(new Notification());
		notifications.add(new Notification());
		notifications.add(new Notification());
		notifications.add(new Notification());
		user.setNotifications(notifications);
		
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
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		Feed<Notification> notifications = notificationService.getNotifications("test");
		assertThat(notifications.getElements().size(), equalTo(2));
	}
	
	@Test
	public void testGetManyNotifications() throws Exception {
		User user = mockUser("test", "password");
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		Feed<Notification> notifications = notificationService.getNotifications("test");
		assertThat(notifications.getElements().size(), equalTo(8));
	}
	
	@Test
	public void testProcessKluchWithNoNotifications() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("some text no mention.");
		List<Notification> notifications = notificationService.processKluch(kluch);
		assertThat(notifications.size(), equalTo(0));	
	}
	
	@Test
	public void testProcessKluchWithValidNotification() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser");
		mockUsers(Arrays.asList("anotheruser"));
		List<Notification> notifications = notificationService.processKluch(kluch);
		assertThat(notifications.size(), equalTo(1));	
	}
	
	@Test
	public void testProcessKluchWithMultipleMentions() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @differentuser");
		mockUsers(Arrays.asList("anotheruser", "differentuser"));
		List<Notification> notifications = notificationService.processKluch(kluch);
		assertThat(notifications.size(), equalTo(2));
	}
	
	@Test
	public void testProcessKluchWithMultipleMentionsAgain() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @user_1@user @user @abc@abc @a @a");
		mockUsers(Arrays.asList("anotheruser", "user_1", "user", "abc", "a"));
		List<Notification> notifications = notificationService.processKluch(kluch);
		assertThat(notifications.size(), equalTo(5));
	}
	
	@Test
	public void testProcessKluchWithNonExistentUserMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser");
		List<Notification> notifications = notificationService.processKluch(kluch);
		assertThat(notifications.size(), equalTo(0));	
	}
	
	@Test
	public void testProcessKluchWithMultipleOfSameMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @anotheruser");
		// notification service retrieves a list of users in
		// kluch text in one DB call, so we want to mock
		mockUsers(Arrays.asList("anotheruser"));
		List<Notification> notifications = notificationService.processKluch(kluch);
		assertThat(notifications.size(), equalTo(1));	
	}
	
	@Test
	public void testProcessKluchWithTwoSetsOfSameMention() throws Exception {
		Kluch kluch = mock(Kluch.class);
		when(kluch.getText()).thenReturn("@anotheruser @anotheruser @a @a");
		mockUsers(Arrays.asList("anotheruser", "a"));
		List<Notification> notifications = notificationService.processKluch(kluch);
		assertThat(notifications.size(), equalTo(2));	
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
		Notification notification = notificationService.addFollowNotification("one", "two");
		assertThat(notification.getFollow(), equalTo("one"));
	}
	
	@Test
	public void testAddFollowNotificationMultipleValid() throws Exception {
		mockUser("one", "password");
		mockUser("two", "password");
		Notification notification = notificationService.addFollowNotification("one", "two");
		assertThat(notification.getFollow(), equalTo("one"));
		mockUser("three", "password");
		notification = notificationService.addFollowNotification("one", "three");
		assertThat(notification.getFollow(), equalTo("one"));
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
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		user.getNotifications().add(new Notification());
		notificationService.read("user");
		for(Notification n: user.getNotifications()) {
			if(!n.isRead()) {
				fail("All notifications should be read.");
			}
		}
	}

}