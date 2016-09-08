package com.soze.notification.service;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.transaction.Transactional;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.soze.TestWithRealUsers;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class NotificationServiceTimeTest extends TestWithRealUsers {

	private final Set<String> usernames = new HashSet<>();
	private final int usernamesToCreate = 64;
	private final Random random = new Random();
	
	@Autowired
	@Qualifier("SimpleNotificationService")
	private NotificationService notificationService;
	
	@Autowired
	@Qualifier("NotificationServiceWithCache")
	private NotificationService notificationServiceWithCache;
	
	@Before
	public void setUp() {
		for(int i = 0; i < usernamesToCreate; i++) {
			usernames.add(createRandomUser());
		}
	}
	
	private String createRandomUser() {
		String username = createRandomName();
		addUser(username, "password");
		return username;
	}
	
	private String createRandomName() {
		int length = 8;
		String alphabet = "qwertyuiopasdfghjklzxcvbnm";	
		String name = "";
		for(int i = 0; i < length; i++) {
			name += alphabet.charAt(random.nextInt(alphabet.length()));
		}
		if(usernames.contains(name)) {
			return createRandomName();
		}
		return name;
	}
	
	@Test
	@Ignore
	public void timeTestUncachedPoll() throws Exception {
		String username = "test";
		addUser(username, "password");
		addNotifications(username);
		for(int i = 0; i < 5; i++) {
			testUncached(username, true);
		}
		testUncached(username, false);
	}
	
	@Test
	@Ignore
	public void timeTestCachedPoll() throws Exception {
		String username = "test";
		addUser(username, "password");
		addNotificationsCache(username);
		for(int i = 0; i < 5; i++) {
			testCached(username, true);
		}
		testCached(username, false);
	}
	
	private void testUncached(String username, boolean warmUp) {
		long startMs = System.currentTimeMillis();
		int iterations = 500_000;
		int sum = 0;
		for(int i = 0; i < iterations; i++) {
			sum += notificationService.poll(username);
		}
		long totalMs = System.currentTimeMillis() - startMs;
		System.out.println("[UNCACHED] Sum was " + sum + ". total time taken in ms was " + totalMs + ". warmup ? " + warmUp);
	}
	
	private void testCached(String username, boolean warmUp) {
		long startMs = System.currentTimeMillis();
		int iterations = 500_000;
		int sum = 0;
		for(int i = 0; i < iterations; i++) {
			sum += notificationServiceWithCache.poll(username);
		}
		long totalMs = System.currentTimeMillis() - startMs;
		System.out.println("[CACHED] Sum was " + sum + ". total time taken in ms was " + totalMs + ". warmup ? " + warmUp);
	}
	
	private void addNotifications(String username) {
		for(String user: usernames) {
			notificationService.addFollowNotification(user, username);
		}
	}
	
	private void addNotificationsCache(String username) {
		for(String user: usernames) {
			notificationServiceWithCache.addFollowNotification(user, username);
		}
	}
	
}
