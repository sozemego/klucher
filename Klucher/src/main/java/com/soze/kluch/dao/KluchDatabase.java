package com.soze.kluch.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.soze.hashtag.model.Hashtag;
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
  public boolean exists(long id) {
    return kluchRepository.exists(id);
  }

  @Override
  public Iterable<Kluch> findAll() {
    return kluchRepository.findAll();
  }

  @Override
  public Iterable<Kluch> findAll(Iterable<Long> ids) {
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
  public List<Kluch> findByAuthor(String author) {
    return kluchRepository.findByAuthor(author);
  }
  
  @Override
  public List<Kluch> findByAuthorOrderByTimestampDesc(String author) {
    return kluchRepository.findByAuthorOrderByTimestampDesc(author);
  }

  @Override
  public List<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author) {
    return kluchRepository.findTop20ByAuthorOrderByTimestampDesc(author);
  }
  
  @Override
  public Page<Kluch> findTop20ByAuthorOrderByTimestampDesc(String author, Pageable pageRequest) {
    return kluchRepository.findTop20ByAuthorOrderByTimestampDesc(author, pageRequest);
  }
  
  @Override
  public Page<Kluch> findByAuthorOrderByTimestampDesc(String author, Pageable pageRequest) {
    return kluchRepository.findByAuthorOrderByTimestampDesc(author, pageRequest);
  }
  
  @Override
  public Page<Kluch> findByAuthorInOrderByTimestampDesc(Iterable<String> authors, Pageable pageRequest) {
    return kluchRepository.findByAuthorInOrderByTimestampDesc(authors, pageRequest);
  }
  
  @Override
  public Page<Kluch> findByAuthorInAndTimestampLessThan(Iterable<String> authors, Timestamp lessThan, Pageable pageRequest) {
    return kluchRepository.findByAuthorInAndTimestampLessThan(authors, lessThan, pageRequest);
  }
  
  @Override
  public Page<Kluch> findByAuthorInAndTimestampGreaterThan(Iterable<String> authors, Timestamp lessThan, Pageable pageRequest) {
    return kluchRepository.findByAuthorInAndTimestampGreaterThan(authors, lessThan, pageRequest);
  }
  
  @Override
  public void deleteByAuthor(String author) {
    kluchRepository.deleteByAuthor(author);
  }

  @Override
  public Page<Kluch> findByHashtagsInAndTimestampLessThan(Hashtag hashtag, Timestamp lessThan,
      Pageable pageRequest) {
    return kluchRepository.findByHashtagsInAndTimestampLessThan(hashtag, lessThan, pageRequest);
  }

  
  
   
}
