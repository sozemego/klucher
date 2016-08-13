package com.soze.hashtag.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
  
  @Async("kluchProcessingExecutor")
  public void process(Kluch kluch) {
    Set<String> hashtagsTexts = extractHashtags(kluch.getText());
    if(hashtagsTexts.isEmpty()) {
      return;
    }
    List<Hashtag> hashtags = getHashtags(hashtagsTexts, kluch);
    saveHashtags(hashtags);
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
  

  private List<Hashtag> getHashtags(Set<String> hashtagTexts, Kluch kluch) {
    List<Hashtag> hashtags = new ArrayList<>();
    for(String hashtag: hashtagTexts) {
      hashtags.add(generateHashtag(hashtag, kluch));
    }
    return hashtags;
  }
  
  private Hashtag generateHashtag(String hashtagText, Kluch kluch) {
    Hashtag hashtag = hashtagRepository.findOne(hashtagText);
    if(hashtag == null) {
      hashtag = new Hashtag();     
    }
    hashtag.setText(hashtagText);
    hashtag.getKluchs().add(kluch);
    return hashtag;
  }
  
  private void saveHashtags(List<Hashtag> hashtags) {
    hashtagRepository.save(hashtags);
  }
 
}
