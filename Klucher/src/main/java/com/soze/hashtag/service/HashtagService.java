package com.soze.hashtag.service;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soze.hashtag.model.Hashtag;
import com.soze.hashtag.repository.HashtagRepository;
import com.soze.kluch.model.Kluch;

/**
 * A service which looks for hashtags in kluchs and
 * adds entries to DB with hashtags and Kluch id's.
 * @author sozek
 *
 */
@Service
public class HashtagService {

  private final HashtagRepository hashtagRepository;
  private final Pattern hashtagExtractor = Pattern.compile("(#\\w+)");
  
  @Autowired
  public HashtagService(HashtagRepository hashtagRepository) {
    this.hashtagRepository = hashtagRepository;
  }
  
  @Async
  public void process(Kluch kluch) {
    Set<String> hashtags = extractHashtags(kluch.getText());
    if(hashtags.isEmpty()) {
      return;
    }
    for(String hashtag: hashtags) {
      addHashtag(hashtag, kluch);
    }
  }
  
  private Set<String> extractHashtags(String kluchText) {
    Matcher matcher = hashtagExtractor.matcher(kluchText);
    Set<String> hashtags = new HashSet<>();
    while(matcher.find()) {
      String hashtag = matcher.group(1);
      hashtags.add(hashtag);
    }
    return hashtags;
  }
  
  @Transactional
  private void addHashtag(String hashtagText, Kluch kluch) {
    Hashtag hashtag = hashtagRepository.findOne(hashtagText);
    if(hashtag == null) {
      hashtag = new Hashtag();     
    }
    hashtag.setText(hashtagText);
    hashtag.getKluchs().add(kluch);
    hashtagRepository.save(hashtag);
  }
  
  
  
}
