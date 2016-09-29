package com.soze.follow.dao;

import java.util.List;

import com.soze.follow.model.Follow;

public interface FollowDao {

	public List<Follow> findAllByFollowerId(Long followerId);

	public List<Follow> findAllByFolloweeId(Long followeeId);
	
	public Follow findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
	
	public void delete(Follow follow);
	
	public void delete(Long id);
	
	public Follow save(Follow follow);

}
