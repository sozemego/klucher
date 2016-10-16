package com.soze.kluch.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.soze.kluch.model.Kluch;

public interface KluchDao {

	public Kluch save(Kluch kluch);

	public Iterable<Kluch> save(Iterable<Kluch> kluchs);

	public Kluch findOne(long id);

	public List<Kluch> findAll(Iterable<Long> ids);

	public long count();

	public void delete(long id);

	public void delete(Kluch kluch);

	public void delete(Iterable<Kluch> kluchs);

	public void deleteAll();

	public Page<Kluch> findByAuthorIdInAndIdGreaterThan(Iterable<Long> authorIds, long greaterThanId, Pageable pageRequest);
	
	public Page<Kluch> findByAuthorIdInAndIdLessThan(Iterable<Long> authorIds, long lessThanId, Pageable pageRequest);
	
	public Page<Kluch> findByMentionsInAndIdLessThan(String mention, long lessThanId, Pageable pageRequest);
	
	public Page<Kluch> findByHashtagsInAndIdLessThan(String hashtag, long lessThanId, Pageable pageRequest);

	public void deleteByAuthorId(Long authorId);
	
	public Long countByAuthorId(Long authorId);

}
