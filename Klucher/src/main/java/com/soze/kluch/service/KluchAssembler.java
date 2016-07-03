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
   * This method removes all non-dictionary characters from the KluchText.
   * @param author
   * @param kluchText
   * @return
   */
  public Kluch assembleKluch(String author, String kluchText) throws InvalidKluchContentException {
    validateKluch(kluchText);
    kluchText = stripNonDictionaryCharacters(kluchText);
    Kluch kluch = new Kluch();
    kluch.setAuthor(author);
    kluch.setText(kluchText);
    kluch.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
    return kluch;
  }
  
  private void validateKluch(String kluchText) throws InvalidKluchContentException {
    if(kluchText.length() > KLUCH_MAX_LENGTH) {
      throw new InvalidKluchContentException("Kluch content is too long.");
    }
  }
  
  private String stripNonDictionaryCharacters(String kluchText) {
    return kluchText.replaceAll("[^\\p{IsAlphabetic}^\\p{IsDigit}]", "");
  }
  
  
}
