package com.soze.follow.service;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

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
import com.soze.follow.model.Follow;
import com.soze.user.model.User;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class FollowServiceTest extends TestWithMockUsers {
	
	@Autowired
	private FollowService followService;
	
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
		mockUser("user");
		followService.follow("user", "user");
	}
	
	@Test
	public void testValidUsersFollow() throws Exception {
		User follower = mockUser("follower");
		User followee = mockUser("followee");
		Follow follow = followService.follow("follower", "followee");
		assertThat(follow.getFollowerId(), equalTo(follower.getId()));
		assertThat(follow.getFolloweeId(), equalTo(followee.getId()));
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
		mockUser("user");
		followService.unfollow("user", "user");
	}
	
	@Test
	public void testValidUsersUnfollow() throws Exception {
		User follower = mockUser("follower");
		User followee = mockUser("followee");
		Follow follow = followService.follow("follower", "followee");
		assertThat(follow.getFollowerId(), equalTo(follower.getId()));
		assertThat(follow.getFolloweeId(), equalTo(followee.getId()));
	}
}
