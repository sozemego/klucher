package com.soze.kluch.dao;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soze.kluch.model.Kluch;
import com.soze.kluch.repository.KluchRepository;

@Service
public class KluchDatabase implements KluchDao {

	private final KluchRepository kluchRepository;

	@Autowired
	public KluchDatabase(KluchRepository kluchRepository) {
		this.kluchRepository = kluchRepository;
	}

	@Override
	@CacheEvict(cacheNames = "kluchCount", key = "#kluch.authorId")
	public Kluch save(Kluch kluch) {
		return kluchRepository.save(kluch);
	}

	@Override
	public Iterable<Kluch> save(Iterable<Kluch> kluchs) {
		return kluchRepository.save(kluchs);
	}

	@Override
	public Kluch findOne(long id) {
		return kluchRepository.findOne(id);
	}

	@Override
	public List<Kluch> findAll(Iterable<Long> ids) {
		return kluchRepository.findAll(ids);
	}

	@Override
	public long count() {
		return kluchRepository.count();
	}

	@Override
	public void delete(long id) {
		kluchRepository.delete(id);
	}

	@Override
	public void delete(Kluch kluch) {
		kluchRepository.delete(kluch);
	}

	@Override
	public void delete(Iterable<Kluch> kluchs) {
		kluchRepository.delete(kluchs);
	}

	@Override
	public void deleteAll() {
		kluchRepository.deleteAll();
	}

	@Override
	public Page<Kluch> findByAuthorIdInAndIdGreaterThan(Iterable<Long> authorIds, long greaterThanId, Pageable pageRequest) {
		return kluchRepository.findByAuthorIdInAndIdGreaterThan(authorIds, greaterThanId, pageRequest);
	}
	
	@Override
	public Page<Kluch> findByAuthorIdInAndIdLessThan(Iterable<Long> authorIds, long lessThanId, Pageable pageRequest) {
		return kluchRepository.findByAuthorIdInAndIdLessThan(authorIds, lessThanId, pageRequest);
	}
	
	@Override
	public Page<Kluch> findByMentionsInAndIdLessThan(String mention, long lessThanId, Pageable pageRequest) {
		return kluchRepository.findByMentionsInAndIdLessThan(mention, lessThanId, pageRequest);
	}
	
	@Override
	public Page<Kluch> findByHashtagsInAndIdLessThan(String hashtag, long lessThanId, Pageable pageRequest) {
		return kluchRepository.findByHashtagsInAndIdLessThan(hashtag, lessThanId, pageRequest);
	}

	@Override
	public void deleteByAuthorId(Long authorId) {
		kluchRepository.deleteByAuthorId(authorId);
	}
	
	@Override
	@Cacheable(cacheNames = "kluchCount")
	public Long countByAuthorId(Long authorId) {
		return kluchRepository.countByAuthorId(authorId);
	}

}
