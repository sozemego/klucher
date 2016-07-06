package com.soze.kluch.service;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.soze.kluch.exceptions.InvalidKluchContentException;
import com.soze.kluch.model.Kluch;

/**
 * Assembles a {@link Kluch} object from a String.
 * @author sozek
 *
 */
@Service
public class KluchAssembler {

  private static final int KLUCH_MAX_LENGTH = 250;
  
  /**
   * Assembles and returns a valid Kluch object for a given author and text.
   * @param author cannot be empty or null
   * @param kluchText cannot be empty or null
   * @return
   * @throws InvalidKluchContentException if kluchText is null, empty or too long
   * @throws IllegalArgumentException if author is null or empty
   */
  public Kluch assembleKluch(String author, String kluchText) throws InvalidKluchContentException, IllegalArgumentException {
    validateAuthor(author);
    validateKluch(kluchText);
    Kluch kluch = new Kluch();
    kluch.setAuthor(author);
    kluch.setText(kluchText);
    kluch.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
    return kluch;
  }
  
  private void validateAuthor(String author) {
    if(author == null) {
      throw new IllegalArgumentException("Author name cannot be null.");
    }
    if(author.isEmpty()) {
      throw new IllegalArgumentException("Author name cannot be empty.");
    }
  }
  
  private void validateKluch(String kluchText) throws InvalidKluchContentException {
    if(kluchText == null) {
      throw new InvalidKluchContentException("Kluch content cannot be null.");
    }
    if(kluchText.length() > KLUCH_MAX_LENGTH) {
      throw new InvalidKluchContentException("Kluch content is too long.");
    }
    if(kluchText.isEmpty()) {
      throw new InvalidKluchContentException("Kluch cannot be empty.");
    }
  }

}
