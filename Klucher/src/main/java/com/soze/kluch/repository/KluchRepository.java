package com.soze.kluch.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.model.Kluch;

public interface KluchRepository extends JpaRepository<Kluch, Long> {

	public List<Kluch> findByAuthor(String author);

	public List<Kluch> findByAuthorOrderByTimestampDesc(String author);

	public List<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author);

	public List<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author, Pageable pageRequest);

	public List<Kluch> findByAuthorOrderByTimestampDesc(String author, Pageable pageRequest);

	public List<Kluch> findByAuthorInOrderByTimestampDesc(Iterable<String> authors, Pageable pageRequest);

	public Page<Kluch> findByAuthorInAndTimestampLessThan(Iterable<String> authors, Timestamp lessThan,
			Pageable pageRequest);

	public Page<Kluch> findByAuthorInAndTimestampGreaterThan(Iterable<String> authors, Timestamp greaterThan,
			Pageable pageRequest);

	public Page<Kluch> findByHashtagsInAndTimestampLessThan(Hashtag hashtag, Timestamp lessThan, Pageable pageRequest);

	public void deleteByAuthor(String author);

}
