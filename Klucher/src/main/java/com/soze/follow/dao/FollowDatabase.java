package com.soze.follow.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.follow.model.Follow;
import com.soze.follow.repository.FollowRepository;

@Service
public class FollowDatabase implements FollowDao {

	private final FollowRepository repository;

	@Autowired
	public FollowDatabase(FollowRepository repository) {
		this.repository = repository;
	}

	@Override
	public List<Follow> findAllByFollowerId(Long followerId) {
		return repository.findAllByFollowerId(followerId);
	}

	@Override
	public List<Follow> findAllByFolloweeId(Long followeeId) {
		return repository.findAllByFolloweeId(followeeId);
	}

	@Override
	public Follow findByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
		return repository.findByFollowerIdAndFolloweeId(followerId, followeeId);
	}

	@Override
	public void delete(Follow follow) {
		repository.delete(follow);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	public Follow save(Follow follow) {
		return repository.save(follow);
	}

}
