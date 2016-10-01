package com.soze.user.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.common.feed.Feed;
import com.soze.follow.dao.FollowDao;
import com.soze.follow.model.Follow;
import com.soze.kluch.model.FeedRequest;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;
import com.soze.user.model.UserFollowerView;

@Service
public class UserFeedService {

	private static final int ELEMENTS_PER_REQUEST = 30;
	private final UserDao userDao;
	private final FollowDao followDao;
	
	@Autowired
	public UserFeedService(UserDao userDao, FollowDao followDao) {
		this.userDao = userDao;
		this.followDao = followDao;
	}
	
	public Feed<UserFollowerView> getFollowerFeed(String username, FeedRequest feedRequest) throws UserDoesNotExistException, NullOrEmptyException {
		return getFollowerFeed(getUser(username), feedRequest);
	}
	
	public Feed<UserFollowerView> getFollowerFeed(User user, FeedRequest feedRequest) throws NullOrEmptyException {
		if(user == null) {
			throw new NullOrEmptyException("User");
		}
		return getFollowerFeed(user.getId(), feedRequest);
	}
	
	public Feed<UserFollowerView> getFollowerFeed(long userId, FeedRequest feedRequest) throws NullOrEmptyException {
		if(feedRequest == null) {
			throw new NullOrEmptyException("FeedRequest");
		}
		List<Follow> follows = followDao.findAllByFolloweeId(userId);
		return constructFollowerFeed(follows, feedRequest);
	}
	
	private Feed<UserFollowerView> constructFollowerFeed(List<Follow> follows, FeedRequest feedRequest) {
		List<Follow> filteredFollows = filterFollows(follows, feedRequest);
		List<Long> followerIds = getFollowerIds(filteredFollows, feedRequest);
		List<User> followers = userDao.findAll(followerIds);
		List<UserFollowerView> userFollowerViews = constructFollowerFeed(followers);
		Long next = getNextIdFollowers(filteredFollows, follows.size());
		return new Feed<>(userFollowerViews, null, next, follows.size());
	}
	
	private List<Follow> filterFollows(List<Follow> follows, FeedRequest feedRequest) {
		return follows.stream()
				.filter(follow -> follow.getId() < feedRequest.getId())
				.limit(ELEMENTS_PER_REQUEST)
				.collect(Collectors.toList());
	}
	
	private List<Long> getFollowerIds(List<Follow> follows, FeedRequest feedRequest) {
		return follows.stream()
				.map(follow -> follow.getFollowerId())
				.collect(Collectors.toList());
	}
	
	private List<UserFollowerView> constructFollowerFeed(List<User> users) {
		List<UserFollowerView> userFollowerViews = new ArrayList<>();
		users.forEach(user -> {
			UserFollowerView view = new UserFollowerView(user.getUsername(), user.getAvatarPath());
			userFollowerViews.add(view);
		});
		
		return userFollowerViews;
	}
	
	private Long getNextIdFollowers(List<Follow> filteredFollows, int total) {
		if(filteredFollows.size() < ELEMENTS_PER_REQUEST || filteredFollows.size() == total) {
			return null;
		}
		return filteredFollows.get(filteredFollows.size() - 1).getFollowerId();
	}
	
	private User getUser(String username) throws UserDoesNotExistException, NullOrEmptyException {
		if(username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
		User user = userDao.findOne(username);
		if(user == null) {
			throw new UserDoesNotExistException(username);
		}
		return user;
	}
	
}
