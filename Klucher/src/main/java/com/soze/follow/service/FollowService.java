package com.soze.follow.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.common.exceptions.CannotDoItToYourselfException;
import com.soze.common.exceptions.NullOrEmptyException;
import com.soze.common.exceptions.UserDoesNotExistException;
import com.soze.follow.dao.FollowDao;
import com.soze.follow.model.Follow;
import com.soze.user.dao.UserDao;
import com.soze.user.model.User;

/**
 * Service which allows users to follow other users and be followed by other
 * users.
 * 
 * @author sozek
 *
 */
@Service
public class FollowService {

	private final UserDao userDao;
	private final FollowDao followDao;

	@Autowired
	public FollowService(UserDao userDao, FollowDao followDao) {
		this.userDao = userDao;
		this.followDao = followDao;
	}

	/**
	 * Creates and persists a {@link Follow} entity, which represents a
	 * follower/followee relationship.
	 * 
	 * @param username
	 *          name of the user who followed (follower)
	 * @param follow
	 *          name of the user who was followed (followee)
	 * @throws NullOrEmptyException
	 *           if either username or follow are null or empty
	 * @throws UserDoesNotExistException
	 *           if either username or follow do not exist
	 * @throws CannotDoItToYourselfException
	 *           if username equals to follow
	 */
	public Follow follow(String follower, String followee)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		validateInput(follower, followee);
		User user = userDao.findOne(follower);
		if (user == null) {
			throw new UserDoesNotExistException(follower);
		}
		User followUser = userDao.findOne(followee);
		if (followUser == null) {
			throw new UserDoesNotExistException(followee);
		}
		Follow followEntity = new Follow();
		followEntity.setFollowerId(user.getId());
		followEntity.setFolloweeId(followUser.getId());
		return followDao.save(followEntity);
	}

	/**
	 * If user named username follows user with name follow, finds and delete the
	 * relationship between them.
	 * 
	 * @param username
	 * @param follow
	 * @return removed Follow entity or null if it didn't exist
	 * @throws NullOrEmptyException
	 *           if username or follow are either null or empty
	 * @throws UserDoesNotExistException
	 *           if either username or follow do not exist
	 * @throws CannotDoItToYourselfException
	 *           if username equals follow
	 */
	public Follow unfollow(String username, String follow)
			throws NullOrEmptyException, UserDoesNotExistException, CannotDoItToYourselfException {
		validateInput(username, follow);
		User user = userDao.findOne(username);
		if (user == null) {
			throw new UserDoesNotExistException(username);
		}
		User followUser = userDao.findOne(follow);
		if (followUser == null) {
			throw new UserDoesNotExistException(follow);
		}
		Follow f = followDao.findByFollowerIdAndFolloweeId(user.getId(), followUser.getId());
		if (f != null) {
			followDao.delete(f);
		}
		return f;
	}

	/**
	 * Checks whether a user with given username follows user follow.
	 * 
	 * @param username
	 * @param follow
	 * @return
	 */
	public boolean doesUsernameFollow(String username, User follow) {
		User user = getUser(username);
		Follow f = followDao.findByFollowerIdAndFolloweeId(user.getId(), follow.getId());
		if (f != null) {
			return true;
		}
		return false;
	}

	private User getUser(String username) throws UserDoesNotExistException {
		User user = userDao.findOne(username);
		if (user == null) {
			throw new UserDoesNotExistException("There is no user named " + username);
		}
		return user;
	}

	private void validateInput(String username, String follow)
			throws NullOrEmptyException, CannotDoItToYourselfException {
		if (username == null || username.isEmpty()) {
			throw new NullOrEmptyException("Username");
		}
		if (follow == null || follow.isEmpty()) {
			throw new NullOrEmptyException("User you are trying to follow");
		}
		if (username.equals(follow)) {
			throw new CannotDoItToYourselfException(username, "follow/unfollow");
		}
	}

}
