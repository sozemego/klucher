package com.soze.hashtag.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.hashtag.model.Hashtag;
import com.soze.hashtag.repository.HashtagRepository;

@Service
public class HashtagDatabase implements HashtagDao {

  private final HashtagRepository hashtagRepository;
  
  @Autowired
  public HashtagDatabase(HashtagRepository hashtagRepository) {
    this.hashtagRepository = hashtagRepository;
  }
  
  @Override
  public Hashtag findOne(String text) {
    return hashtagRepository.findOne(text);
  }
  
}
