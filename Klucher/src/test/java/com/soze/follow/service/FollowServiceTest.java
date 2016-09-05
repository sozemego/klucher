package com.soze.follow.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

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
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FollowServiceTest extends TestWithMockUsers {
	
	@Autowired
	private FollowService followService;
	
	@Autowired
	private UserDao userDao;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testNullUsernameFollow() throws Exception {
		followService.follow(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testEmptyUsernameFollow() throws Exception {
		followService.follow("", null);
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testUserDoesNotExistFollow() throws Exception {
		followService.follow("user", "user2");
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testNullFollowFollow() throws Exception {
		mockUser("user");
		followService.follow("user", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testEmptyFollowFollow() throws Exception {
		mockUser("user");
		followService.follow("user", "");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testFollowDoesNotExistFollow() throws Exception {
		mockUser("user");
		followService.follow("user", "user2");
	}
	
	@Test(expected = CannotDoItToYourselfException.class)
	public void testUsernameAndFollowEqualFollow() throws Exception {
		followService.follow("user", "user");
	}
	
	@Test
	public void testValidUsersFollow() throws Exception {
		List<User> users = mockUsers(Arrays.asList("user", "user1"));
		User user = users.get(0);
		User user1 = users.get(1);
		followService.follow("user", "user1");
		verify(userDao).save(users);
		assertThat(user.getFollowing().contains(user1.getUsername()), equalTo(true));
		assertThat(user1.getFollowers().contains(user.getUsername()), equalTo(true));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testNullUsernameUnfollow() throws Exception {
		followService.unfollow(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testEmptyUsernameUnfollow() throws Exception {
		followService.unfollow("", null);
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testUserDoesNotExistUnfollow() throws Exception {
		followService.unfollow("user", "user2");
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testNullFollowUnfollow() throws Exception {
		mockUser("user");
		followService.unfollow("user", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testEmptyFollowUnfollow() throws Exception {
		mockUser("user");
		followService.unfollow("user", "");
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testFollowDoesNotExistUnfollow() throws Exception {
		mockUser("user");
		followService.unfollow("user", "user2");
	}
	
	@Test(expected = CannotDoItToYourselfException.class)
	public void testUsernameAndFollowEqualUnfollow() throws Exception {
		followService.unfollow("user", "user");
	}
	
	@Test
	public void testValidUsersUnfollow() throws Exception {
		List<User> users = mockUsers(Arrays.asList("user", "user1"));
		User user = users.get(0);
		user.getFollowing().add("user1");
		User user1 = users.get(1);
		user1.getFollowers().add("user");
		followService.unfollow("user", "user1");
		verify(userDao).save(users);
		assertThat(user.getFollowing().contains(user1.getUsername()), equalTo(false));
		assertThat(user1.getFollowers().contains(user.getUsername()), equalTo(false));
	}
}
