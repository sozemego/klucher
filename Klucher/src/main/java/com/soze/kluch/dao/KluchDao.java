package com.soze.kluch.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.model.Kluch;

public interface KluchDao {

  public Kluch save(Kluch kluch);

  public Iterable<Kluch> save(Iterable<Kluch> kluchs);

  public Kluch findOne(long id);

  public boolean exists(long id);

  public Iterable<Kluch> findAll();

  public Iterable<Kluch> findAll(Iterable<Long> ids);

  public long count();

  public void delete(long id);

  public void delete(Kluch kluch);

  public void delete(Iterable<Kluch> kluchs);

  public void deleteAll();
  
  public List<Kluch> findByAuthor(String author);
  
  public List<Kluch> findByAuthorOrderByTimestampDesc(String author);
  
  public Page<Kluch> findByAuthorOrderByTimestampDesc(String author, Pageable pageRequest);
  
  public Page<Kluch> findByAuthorInOrderByTimestampDesc(Iterable<String> authors, Pageable pageRequest);
  
  public List<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author);
  
  public Page<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author, Pageable pageRequest);
  
  public Page<Kluch> findByAuthorInAndTimestampLessThan(Iterable<String> authors, Timestamp lessThan, Pageable pageRequest);
  
  public Page<Kluch> findByAuthorInAndTimestampGreaterThan(Iterable<String> authors, Timestamp lessThan, Pageable pageRequest);
  
  public void deleteByAuthor(String author);
  
  public Page<Kluch> findByHashtagsInAndTimestampLessThan(Hashtag hashtag, Timestamp lessThan, Pageable pageRequest);
  
}
