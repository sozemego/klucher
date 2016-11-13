package com.soze.kluch.dao;

import java.sql.Timestamp;
import java.util.List;

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

	public List<Kluch> findByAuthorIdInAndIdGreaterThan(Iterable<Long> authorIds, long greaterThanId, Pageable pageRequest);
	
	public List<Kluch> findByAuthorIdInAndIdLessThan(Iterable<Long> authorIds, long lessThanId, Pageable pageRequest);
	
	public List<Kluch> findByMentionsInAndIdLessThan(String mention, long lessThanId, Pageable pageRequest);
	
	public List<Kluch> findByHashtagsInAndIdLessThan(String hashtag, long lessThanId, Pageable pageRequest);
	
	public Long countByAuthorId(Long authorId);
	
	public List<Kluch> findAllAfterTimestamp(Timestamp timestamp);

}
