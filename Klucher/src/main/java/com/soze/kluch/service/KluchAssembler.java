package com.soze.kluch.service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.hashtag.service.HashtagService;
import com.soze.kluch.model.Kluch;

/**
 * Assembles a {@link Kluch} object from a String.
 * @author sozek
 *
 */
@Service
public class KluchAssembler {
  
  private final HashtagService hashtagService;
  
  @Autowired
  public KluchAssembler(HashtagService hashtagService) {
    this.hashtagService = hashtagService;
  }
  
  /**
   * Assembles and returns a Kluch object. No validation is done here.
   * @param author
   * @param kluchText
   * @return a <code>Kluch</code> entity, with all fields required for DB persistence
   */
  public Kluch assembleKluch(String author, String kluchText) {
    Kluch kluch = new Kluch();
    kluch.setAuthor(author);
    kluch.setText(kluchText);
    kluch.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
    kluch.setHashtags(new HashSet<>(hashtagService.process(kluch)));
    return kluch;
  }
 
}
