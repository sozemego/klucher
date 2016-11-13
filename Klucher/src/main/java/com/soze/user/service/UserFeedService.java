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
import com.soze.user.model.UserLikeView;

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
	
	/**
	 * Constructs a feed of UserFollowerView objects. These objects represent
	 * information needed to display users following another user (username, avatarPath).
	 * 
	 * @param username
	 * @param feedRequest
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws NullOrEmptyException
	 */
	public Feed<UserFollowerView> getFollowerFeed(String username, FeedRequest feedRequest) throws UserDoesNotExistException, NullOrEmptyException {
		return getFollowerFeed(getUser(username), feedRequest);
	}
	
	/**
	 * Constructs a feed of UserFollowerView objects. These objects represent
	 * information needed to display users following another user (username, avatarPath).
	 * 
	 * @param username
	 * @param feedRequest
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws NullOrEmptyException
	 */
	public Feed<UserFollowerView> getFollowerFeed(User user, FeedRequest feedRequest) throws NullOrEmptyException {
		if(user == null) {
			throw new NullOrEmptyException("User");
		}
		return getFollowerFeed(user.getId(), feedRequest);
	}
	
	/**
	 * Constructs a feed of UserFollowerView objects. These objects represent
	 * information needed to display users following another user (username, avatarPath).
	 * 
	 * @param username
	 * @param feedRequest
	 * @return
	 * @throws UserDoesNotExistException
	 * @throws NullOrEmptyException
	 */
	public Feed<UserFollowerView> getFollowerFeed(long userId, FeedRequest feedRequest) throws NullOrEmptyException {
		if(feedRequest == null) {
			throw new NullOrEmptyException("FeedRequest");
		}
		List<Follow> follows = followDao.findAllByFolloweeId(userId);
		return constructFollowerFeed(follows, feedRequest);
	}
	
	/**
	 * Creates a feed of UserLikeView objects which contain
	 * information about users that liked another user. 
	 * @param username
	 * @param feedRequest
	 * @return
	 * @throws NullOrEmptyException
	 * @throws UserDoesNotExistException
	 */
	public Feed<UserLikeView> getLikeFeed(String username, FeedRequest feedRequest) throws NullOrEmptyException, UserDoesNotExistException {
		User user = getUser(username);
		if(feedRequest == null) {
			throw new NullOrEmptyException("FeedRequest");
		}
		List<Long> userIds = filterLikes(user.getLikes(), feedRequest);
		return constructLikeFeed(userIds, feedRequest, user.getLikes().size());
	}
	
	private List<Long> filterLikes(List<Long> userIds, FeedRequest feedRequest) {
		int indexOf = userIds.indexOf(feedRequest.getId());
		if(indexOf > -1) {
			return userIds.subList(indexOf, Math.min(indexOf + ELEMENTS_PER_REQUEST, userIds.size()));
		}
		if (indexOf == -1 && !userIds.isEmpty()) {
			return userIds.subList(0, Math.min(ELEMENTS_PER_REQUEST, userIds.size()));
		}
		return new ArrayList<>();
	}
	
	private Feed<UserLikeView> constructLikeFeed(List<Long> userIds, FeedRequest feedRequest, int totalElements) {
		List<UserLikeView> userLikeViews = userDao.findLikeViews(userIds);
		Long next = null;
		if(userIds.size() < ELEMENTS_PER_REQUEST || userIds.size() == totalElements) {
			next = null;
		} else {
			next = userIds.get(userIds.size() - 1);
		}
		return new Feed<>(userLikeViews, null, next);
	}
	
	private Feed<UserFollowerView> constructFollowerFeed(List<Follow> follows, FeedRequest feedRequest) {
		List<Follow> filteredFollows = filterFollows(follows, feedRequest);
		List<Long> followerIds = getFollowerIds(filteredFollows, feedRequest);
		List<UserFollowerView> userFollowerViews = userDao.findFollowerViews(followerIds);
		Long next = getNextIdFollowers(filteredFollows, follows.size());
		return new Feed<>(userFollowerViews, null, next);
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
