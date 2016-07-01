package com.soze.kluch.repository;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import com.soze.kluch.model.Kluch;

public interface KluchRepository extends CrudRepository<Kluch, Long>{

  public List<Kluch> findByAuthor(String author);
  public List<Kluch> findByAuthorOrderByTimestampDesc(String author);
  public List<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author);
  public Page<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author, Pageable pageRequest);
  public Page<Kluch> findByAuthorOrderByTimestampDesc(String author, Pageable pageRequest); 
  public Page<Kluch> findByAuthorInOrderByTimestampDesc(Iterable<String> authors, Pageable pageRequest);
  public Page<Kluch> findByAuthorInAndTimestampLessThan(Iterable<String> authors, Timestamp lessThan, Pageable pageRequest);
  public Page<Kluch> findByAuthorInAndTimestampGreaterThan(Iterable<String> authors, Timestamp greaterThan, Pageable pageRequest);
  @Transactional
  public void deleteByAuthor(String author);
  
}
