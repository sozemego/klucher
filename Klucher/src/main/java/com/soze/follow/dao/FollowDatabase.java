package com.soze.follow.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
	@Cacheable(cacheNames = "followers")
	public List<Follow> findAllByFollowerId(Long followerId) {
		return repository.findAllByFollowerId(followerId);
	}

	@Override
	@Cacheable(cacheNames = "followees")
	public List<Follow> findAllByFolloweeId(Long followeeId) {
		return repository.findAllByFolloweeId(followeeId);
	}

	@Override
	@Cacheable(cacheNames = "followRelationships", key = "{#followerId, #followeeId}")
	public Follow findByFollowerIdAndFolloweeId(Long followerId, Long followeeId) {
		return repository.findByFollowerIdAndFolloweeId(followerId, followeeId);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames = "followers", key = "#follow.followerId"),
			@CacheEvict(cacheNames = "followees", key = "#follow.followeeId"),
			@CacheEvict(cacheNames = "followRelationships", key = "{#follow.followerId, #follow.followeeId}")})
	public void delete(Follow follow) {
		repository.delete(follow);
	}

	@Override
	public void delete(Long id) {
		repository.delete(id);
	}

	@Override
	@Caching(evict = {
			@CacheEvict(cacheNames = "followers", key = "#follow.followerId"),
			@CacheEvict(cacheNames = "followees", key = "#follow.followeeId")},
					put = {
			@CachePut(cacheNames = "followRelationships", key = "{#follow.followerId, #follow.followeeId}")
					})
	public Follow save(Follow follow) {
		return repository.save(follow);
	}

}
