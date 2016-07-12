package com.soze.kluch.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;
import com.soze.kluch.model.KluchResult;

/**
 * A service responsible for posting new Kluchs. It validates them, assembles
 * them and in the future will extract hashtag information and send it off to
 * another service (work in progress). It also stores previous Kluch(s) created
 * by an user (to prevent spam). It also includes rate-limiting functionality.
 * 
 * @author sozek
 *
 */
@Service
public class KluchService {

  private static final Logger log = LoggerFactory.getLogger(KluchService.class);
  private static final int KLUCH_MAX_LENGTH = 250;
  private static final HttpStatus ALREADY_POSTED = HttpStatus.BAD_REQUEST;
  private static final HttpStatus INVALID_AUTHOR = HttpStatus.BAD_REQUEST;
  private static final HttpStatus INVALID_KLUCH_CONTENT = HttpStatus.BAD_REQUEST;
  private final KluchDao kluchDao;
  private final KluchAssembler kluchAssembler;
  private final Map<String, String> pastKluchs = new HashMap<>();

  @Autowired
  public KluchService(KluchDao kluchDao, KluchAssembler kluchAssembler) {
    this.kluchDao = kluchDao;
    this.kluchAssembler = kluchAssembler;
  }

  /**
   * Attempts to post a Kluch with given content for a given User. This method validates
   * input and the result is stored in the KluchResult object.
   * @param username author of given Kluch, cannot be null or empty
   * @param kluchText Kluch content, cannot be null or empty
   */
  public KluchResult post(String username, String kluchText) {
    KluchResult result = new KluchResult(username, kluchText);
    validateInput(username, kluchText, result);
    if(!result.isSuccessful()) {
      return result;
    }
    checkAlreadyPosted(username, kluchText, result);
    if(!result.isSuccessful()) {
      return result;
    }
    Kluch kluch = kluchAssembler.assembleKluch(username, kluchText);
    kluchDao.save(kluch);
    saveLastKluch(username, kluchText);
    log.info("User [{}] successfuly posted a Kluch with text [{}].", username,
        kluchText);
    return result;
  }

  private void checkAlreadyPosted(String username, String kluchText, KluchResult result) {
    String pastKluch = pastKluchs.get(username);
    if (kluchText != null && kluchText.equals(pastKluch)) {
      result.setResult("User's [" + username + "] last Kluch was identical.", ALREADY_POSTED);
    }
  }
  
  private void validateInput(String username, String kluchText, KluchResult result) {
    validateAuthor(username, result);
    validateKluch(kluchText, result);
  }
  
  private void validateAuthor(String author, KluchResult result) {
    if (author == null) {
      result.setResult("Author name cannot be null.", INVALID_AUTHOR);
    } else if (author.isEmpty()) {
      result.setResult("Author name cannot be empty.", INVALID_AUTHOR);
    }
  }
  
  private void validateKluch(String kluchText, KluchResult result) {
    if (kluchText == null) {
      result.setResult("Kluch content cannot be null.", INVALID_KLUCH_CONTENT);
    } else {
      if (kluchText.length() > KLUCH_MAX_LENGTH) {
        result.setResult("Kluch content is too long.", INVALID_KLUCH_CONTENT);
      }
      if (kluchText.isEmpty()) {
        result.setResult("Kluch cannot be empty.", INVALID_KLUCH_CONTENT);
      }
    }
  }


  private void saveLastKluch(String username, String kluchText) {
    pastKluchs.put(username, kluchText);
  }

  public void deleteKluch(long id) {
    kluchDao.delete(id);
    log.info("Deleting a kluch with id [{}].", id);
  }

  public void deleteAll(String username) {
    kluchDao.deleteByAuthor(username);
    log.info("Deleting all Kluchs posted by [{}].", username);
  }

}
