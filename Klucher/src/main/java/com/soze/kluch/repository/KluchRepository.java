package com.soze.kluch.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soze.kluch.model.Kluch;

public interface KluchRepository extends JpaRepository<Kluch, Long> {

	public Page<Kluch> findByAuthorIdInAndIdGreaterThan(Iterable<Long> authorIds, long greaterThanId, Pageable pageRequest);
	
	public Page<Kluch> findByAuthorIdInAndIdLessThan(Iterable<Long> authorIds, long lessThanId, Pageable pageRequest);
	
	public Page<Kluch> findByMentionsInAndIdLessThan(String mention, long lessThanId, Pageable pageRequest);
	
	public Page<Kluch> findByHashtagsInAndIdLessThan(String hashtag, long lessThanId, Pageable pageRequest);

}
