package com.soze.kluch.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soze.kluch.model.Kluch;

public interface KluchRepository extends JpaRepository<Kluch, Long> {

	public List<Kluch> findByAuthorIdInAndIdGreaterThan(Iterable<Long> authorIds, long greaterThanId, Pageable pageRequest);
	
	public List<Kluch> findByAuthorIdInAndIdLessThan(Iterable<Long> authorIds, long lessThanId, Pageable pageRequest);
	
	public List<Kluch> findByMentionsInAndIdLessThan(String mention, long lessThanId, Pageable pageRequest);
	
	public List<Kluch> findByHashtagsInAndIdLessThan(String hashtag, long lessThanId, Pageable pageRequest);

}
