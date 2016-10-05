package com.soze.user.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.follow.dao.FollowDao;
import com.soze.follow.model.Follow;
import com.soze.kluch.model.FeedRequest;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.model.UserFollowerView;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("test")
public class UserFeedServiceTest extends TestWithMockUsers {

	@Autowired
	private UserFeedService userFeedService;

	@Autowired
	private UserDao userDao;

	@MockBean
	private FollowDao followDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullOrEmptyException.class)
	public void testGetFollowerFeedUsernameEmpty() throws Exception {
		userFeedService.getFollowerFeed("", new FeedRequest(null, null));
	}

	@Test(expected = UserDoesNotExistException.class)
	public void testGetFollowerFeedUserDoesNotExist() throws Exception {
		userFeedService.getFollowerFeed("username", new FeedRequest(null, null));
	}

	@Test(expected = NullOrEmptyException.class)
	public void testGetFollowerFeedFeedRequestNull() throws Exception {
		String username = "user";
		mockUser(username);
		userFeedService.getFollowerFeed(username, null);
	}
	
	@Test
	public void testGetFollowerFeedByIdUserDoesNotExist() throws Exception {
		long userId = 54L;
		when(followDao.findAllByFolloweeId(userId)).thenReturn(new ArrayList<>());
		when(userDao.findAll(new ArrayList<>())).thenReturn(new ArrayList<>());
		Feed<UserFollowerView> feed = userFeedService.getFollowerFeed(userId, new FeedRequest(FeedDirection.NEXT, null));
		assertThat(feed, notNullValue());
		assertThat(feed.getElements(), notNullValue());
		assertThat(feed.getElements().size(), equalTo(0));
		assertThat(feed.getNext(), nullValue());
		assertThat(feed.getPrevious(), nullValue());
		assertThat(feed.getTotalElements(), equalTo(0L));
	}
	
	@Test
	public void testGetFollowerFeedByIdUserExistsNoFollowers() throws Exception {
		User user = mockUser("username");
		long userId = user.getId();
		when(followDao.findAllByFolloweeId(userId)).thenReturn(new ArrayList<>());
		when(userDao.findAll(new ArrayList<>())).thenReturn(new ArrayList<>());
		Feed<UserFollowerView> feed = userFeedService.getFollowerFeed(userId, new FeedRequest(FeedDirection.NEXT, null));
		assertThat(feed, notNullValue());
		assertThat(feed.getElements(), notNullValue());
		assertThat(feed.getElements().size(), equalTo(0));
		assertThat(feed.getNext(), nullValue());
		assertThat(feed.getPrevious(), nullValue());
		assertThat(feed.getTotalElements(), equalTo(0L));
	}
	
	@Test
	public void testGetFollowerFeedByIdUserExistsFewFollowers() throws Exception {
		User user = mockUser("username");
		long userId = user.getId();
		int numberOfFollows = 5;
		List<Long> randomUsersIds =  mockRandomUsers(numberOfFollows);
		List<Follow> follows = getFollowersFor(user, randomUsersIds);
		when(followDao.findAllByFolloweeId(userId)).thenReturn(follows);
		Feed<UserFollowerView> feed = userFeedService.getFollowerFeed(userId, new FeedRequest(FeedDirection.NEXT, null));
		assertThat(feed, notNullValue());
		assertThat(feed.getElements(), notNullValue());
		assertThat(feed.getElements().size(), equalTo(5));
		assertThat(feed.getNext(), nullValue());
		assertThat(feed.getPrevious(), nullValue());
		assertThat(feed.getTotalElements(), equalTo(5L));
	}
	
	private List<Long> mockRandomUsers(int number) {
		List<Long> users = new ArrayList<>();
		List<String> usernames = new ArrayList<>();
		for(int i = 0; i < number; i++) {
			User mock = mockRandomUser();
			users.add(mock.getId());
			usernames.add(mock.getUsername());
		}
		mockUsers(usernames, true);
		return users;
	}
	
	private List<Follow> getFollowersFor(User user, List<Long> randomUserIds) {
		List<Follow> follows = new ArrayList<>();
		int i = 0;
		for(Long id: randomUserIds) {
			Follow follow = new Follow(id, user.getId());
			follow.setId(i++);
			follows.add(follow);
		}
		return follows;
	}
	
	

}
