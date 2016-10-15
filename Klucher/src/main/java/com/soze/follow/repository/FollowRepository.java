package com.soze.follow.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.soze.follow.model.Follow;

public interface FollowRepository extends JpaRepository<Follow, Long> {

	public List<Follow> findAllByFollowerId(Long followerId);

	public List<Follow> findAllByFolloweeId(Long followeeId);
	
	public Long countByFolloweeId(Long followeeId);

	public Follow findByFollowerIdAndFolloweeId(Long followerId, Long followeeId);
	
}
