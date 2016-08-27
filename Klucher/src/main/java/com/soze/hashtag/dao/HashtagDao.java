package com.soze.hashtag.dao;

import com.soze.hashtag.model.Hashtag;

public interface HashtagDao {

  public Hashtag findOne(String text);
  
}
