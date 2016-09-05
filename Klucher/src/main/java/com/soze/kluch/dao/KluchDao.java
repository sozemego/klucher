package com.soze.kluch.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.domain.Pageable;

import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.model.Kluch;

public interface KluchDao {

  public Kluch save(Kluch kluch);

  public Iterable<Kluch> save(Iterable<Kluch> kluchs);

  public Kluch findOne(long id);

  public boolean exists(long id);

  public List<Kluch> findAll();

  public List<Kluch> findAll(Iterable<Long> ids);

  public long count();

  public void delete(long id);

  public void delete(Kluch kluch);

  public void delete(Iterable<Kluch> kluchs);

  public void deleteAll();
  
  public List<Kluch> findByAuthor(String author);
  
  public List<Kluch> findByAuthorOrderByTimestampDesc(String author);
  
  public List<Kluch> findByAuthorOrderByTimestampDesc(String author, Pageable pageRequest);
  
  public List<Kluch> findByAuthorInOrderByTimestampDesc(Iterable<String> authors, Pageable pageRequest);
  
  public List<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author);
  
  public List<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author, Pageable pageRequest);
  
  public List<Kluch> findByAuthorInAndTimestampLessThan(Iterable<String> authors, Timestamp lessThan, Pageable pageRequest);
  
  public List<Kluch> findByAuthorInAndTimestampGreaterThan(Iterable<String> authors, Timestamp lessThan, Pageable pageRequest);
  
  public void deleteByAuthor(String author);
  
  public List<Kluch> findByHashtagsInAndTimestampLessThan(Hashtag hashtag, Timestamp lessThan, Pageable pageRequest);
  
}
