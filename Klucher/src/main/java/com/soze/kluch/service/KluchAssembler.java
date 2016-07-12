package com.soze.kluch.service;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.soze.kluch.model.Kluch;

/**
 * Assembles a {@link Kluch} object from a String.
 * @author sozek
 *
 */
@Service
public class KluchAssembler {
  
  /**
   * Assembles and returns a valid Kluch object for a given author and text.
   * @param author
   * @param kluchText
   * @return a valid Kluch, ready to persist to DB
   */
  public Kluch assembleKluch(String author, String kluchText) {
    Kluch kluch = new Kluch();
    kluch.setAuthor(author);
    kluch.setText(kluchText);
    kluch.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
    return kluch;
  }
 
}
