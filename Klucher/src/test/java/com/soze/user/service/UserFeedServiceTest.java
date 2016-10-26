package com.soze.user.service;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.soze.TestWithMockUsers;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.common.feed.Feed;
import com.soze.common.feed.FeedDirection;
import com.soze.follow.dao.FollowDao;
import com.soze.follow.model.Follow;
import com.soze.kluch.model.FeedRequest;
import com.soze.user.model.User;
import com.soze.user.model.UserFollowerView;
import com.soze.user.model.UserLikeView;

@RunWith(SpringRunner.class)
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class UserFeedServiceTest extends TestWithMockUsers {

	@Autowired
	private UserFeedService userFeedService;

	@Autowired
	private FollowDao followDao;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
	}

	@Test(expected = NullOrEmptyException.class)
	public void testGetFollowerFeedUsernameEmpty() throws Exception {
		userFeedService.getFollowerFeed("", new FeedRequest(null, null, Optional.empty()));
	}

	@Test(expected = UserDoesNotExistException.class)
	public void testGetFollowerFeedUserDoesNotExist() throws Exception {
		userFeedService.getFollowerFeed("username", new FeedRequest(null, null, Optional.empty()));
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
		Feed<UserFollowerView> feed = userFeedService.getFollowerFeed(userId, new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
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
		Feed<UserFollowerView> feed = userFeedService.getFollowerFeed(userId, new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
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
		followUser(user, mockRandomUsers(numberOfFollows));
		Feed<UserFollowerView> feed = userFeedService.getFollowerFeed(userId, new FeedRequest(FeedDirection.NEXT, null, Optional.empty()));
		assertThat(feed, notNullValue());
		assertThat(feed.getElements(), notNullValue());
		assertThat(feed.getElements().size(), equalTo(5));
		assertThat(feed.getNext(), nullValue());
		assertThat(feed.getPrevious(), nullValue());
		assertThat(feed.getTotalElements(), equalTo(5L));
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testGetLikeFeedNullUsername() throws Exception {
		userFeedService.getLikeFeed(null, null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testGetLikeFeedEmptyUsername() throws Exception {
		userFeedService.getLikeFeed("", null);
	}
	
	@Test(expected = NullOrEmptyException.class)
	public void testGetLikeFeedNullRequest() throws Exception {
		mockUser("user");
		userFeedService.getLikeFeed("user", null);
	}
	
	@Test(expected = UserDoesNotExistException.class)
	public void testGetLikeFeedUserDoesNotExistException() throws Exception {
		userFeedService.getLikeFeed("user", new FeedRequest(FeedDirection.NEXT, null, null));
	}
	
	@Test
	public void testGetLikeFeedUserValid() throws Exception {
		User user = mockUser("user");
		List<Long> randomUsers = mockRandomUsers(5);
		user.getLikes().addAll(randomUsers);
		Feed<UserLikeView> feed = userFeedService.getLikeFeed("user", new FeedRequest(FeedDirection.NEXT, null, null));
		assertThat(feed, notNullValue());
		assertThat(feed.getElements(), notNullValue());
		assertThat(feed.getElements().size(), equalTo(5));
		assertThat(feed.getNext(), nullValue());
		assertThat(feed.getPrevious(), nullValue());
		assertThat(feed.getTotalElements(), equalTo(5L));
	}
	
	private List<Long> mockRandomUsers(int number) {
		List<Long> users = new ArrayList<>();
		for(int i = 0; i < number; i++) {
			User mock = mockRandomUser();
			users.add(mock.getId());
		}
		return users;
	}
	
	private void followUser(User user, List<Long> randomUserIds) {
		for(Long id: randomUserIds) {
			Follow follow = new Follow(id, user.getId());
			followDao.save(follow);
		}
	}
	
	

}
