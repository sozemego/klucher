package com.soze.kluch.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.hashtag.service.HashtagService;
import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;

/**
 * A service responsible for posting new Kluchs. It validates them, assembles
 * them and in the future will extract hashtag information and send it off to
 * another service (work in progress). It stores previous Kluch(s) created
 * by an user (to prevent spam). Includes rate-limiting functionality.
 * 
 * @author sozek
 *
 */
@Service
public class KluchService {

  private static final Logger log = LoggerFactory.getLogger(KluchService.class);
  private static final int KLUCH_MAX_LENGTH = 250;
  private final KluchDao kluchDao;
  private final KluchAssembler kluchAssembler;
  private final Map<String, String> pastKluchs = new ConcurrentHashMap<>();
  private final HashtagService hashtagService;

  @Autowired
  public KluchService(KluchDao kluchDao, KluchAssembler kluchAssembler, HashtagService hashtagService) {
    this.kluchDao = kluchDao;
    this.kluchAssembler = kluchAssembler;
    this.hashtagService = hashtagService;
  }

  /**
   * Attempts to post a Kluch with given content for a user with given username.
   * @param username author of given Kluch, cannot be null or empty
   * @param kluchText Kluch content, cannot be null or empty
   * @throws IllegalArgumentException if username is null or empty, or if kluch text is null, empty or too long
   */
  public void post(String username, String kluchText) throws IllegalArgumentException {
    validateInput(username, kluchText);
    checkAlreadyPosted(username, kluchText);
    Kluch kluch = kluchAssembler.assembleKluch(username, kluchText);
    kluchDao.save(kluch);
    saveLastKluch(username, kluchText);
    log.info("User [{}] successfuly posted a Kluch with text [{}].", username,
        kluchText);
    hashtagService.process(kluch);
  }

  private void checkAlreadyPosted(String username, String kluchText) {
    String pastKluch = pastKluchs.get(username);
    if (kluchText != null && kluchText.equals(pastKluch)) {
      throw new IllegalArgumentException("User's [" + username + "] last Kluch was identical.");
    }
  }
  
  private void validateInput(String username, String kluchText) throws IllegalArgumentException {
    validateAuthor(username);
    validateKluch(kluchText);
  }
  
  private void validateAuthor(String author) {
    if (author == null || author.isEmpty()) {
      throw new IllegalArgumentException("Author name cannot be null or empty.");
    }
  }
  
  private void validateKluch(String kluchText) {
    if (kluchText == null || kluchText.isEmpty()) {
      throw new IllegalArgumentException("Kluch content cannot be null or empty.");
    } 
    if (kluchText.length() > KLUCH_MAX_LENGTH) {
        throw new IllegalArgumentException("Kluch content is too long.");
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
