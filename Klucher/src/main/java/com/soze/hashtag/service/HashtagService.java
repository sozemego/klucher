package com.soze.hashtag.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.hashtag.dao.HashtagDao;
import com.soze.hashtag.model.Hashtag;
import com.soze.kluch.model.Kluch;

/**
 * A service which extracts {@link Hashtag}s from
 * {@link Kluch}s. 
 * Hashtags are phrases or numbers (they can contain characters a-Z, digits 0-9 and underscore).
 * Hashtags have to be preceded by a space or beginning of input.
 * Hashtags are case insensitive (e.g. #love is the same as #lOvE).
 * @author sozek
 *
 */
@Service
public class HashtagService {

  private static final List<Hashtag> EMPTY_LIST = Arrays.asList();
  private final HashtagDao hashtagDao;
  private final Pattern hashtagExtractor = Pattern.compile("(?:^|\\s)(#\\w+)");
  
  @Autowired
  public HashtagService(HashtagDao hashtagDao) {
    this.hashtagDao = hashtagDao;
  }
  
  /**
   * Extracts hashtags from a Kluch. This kluch is saved in each of the extracted hashtags.
   * @param kluch
   * @return extracted <code>Hashtags</code> from this <code>Kluch</code>
   */
  public List<Hashtag> process(Kluch kluch) {
    Set<String> hashtagsTexts = extractHashtags(kluch.getText());
    if(hashtagsTexts.isEmpty()) {
      return EMPTY_LIST;
    }
    return getHashtags(hashtagsTexts, kluch);
  }
  
  /**
   * Matches regions of text which are hashtags and returns them as a Set.
   * @param kluchText
   * @return
   */
  private Set<String> extractHashtags(String kluchText) {
    Matcher matcher = hashtagExtractor.matcher(kluchText);
    Set<String> hashtags = new HashSet<>();
    while(matcher.find()) {
      String hashtag = matcher.group(1);
      hashtags.add(hashtag);
    }
    return hashtags;
  }
  
  /**
   * Assembles {@link Hashtag} objects from a Set of strings
   * which represent hashtags (e.g. "#ad"). Saves all <code>Hashtags</code>
   * in the Kluch.
   * @param hashtagTexts
   * @param kluch
   * @return
   */
  private List<Hashtag> getHashtags(Set<String> hashtagTexts, Kluch kluch) {
    List<Hashtag> hashtags = new ArrayList<>();
    for(String hashtag: hashtagTexts) {
      hashtags.add(generateHashtag(hashtag.toLowerCase(), kluch));
    }
    return hashtags;
  }
  
  /**
   * Assembles a single {@link Hashtag} from text.
   * @param hashtagText
   * @param kluch
   * @return
   */
  private Hashtag generateHashtag(String hashtagText, Kluch kluch) {
    Hashtag hashtag = hashtagDao.findOne(hashtagText);
    if(hashtag == null) {
      hashtag = new Hashtag(hashtagText);
    }
    hashtag.getKluchs().add(kluch);
    return hashtag;
  }
 
}
