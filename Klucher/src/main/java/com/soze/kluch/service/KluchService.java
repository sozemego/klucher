package com.soze.kluch.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.soze.common.exceptions.InvalidLengthException;
import com.soze.common.exceptions.InvalidLengthException.Adjective;
import com.soze.common.exceptions.KluchPreviouslyPostedException;
import com.soze.common.exceptions.NullOrEmptyException;
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
@Transactional
public class KluchService {

  private static final Logger log = LoggerFactory.getLogger(KluchService.class);
  private static final int KLUCH_MAX_LENGTH = 250;
  private final KluchDao kluchDao;
  private final KluchAssembler kluchAssembler;
  private final Map<String, String> pastKluchs = new ConcurrentHashMap<>();

  @Autowired
  public KluchService(KluchDao kluchDao, KluchAssembler kluchAssembler) {
    this.kluchDao = kluchDao;
    this.kluchAssembler = kluchAssembler;
  }

  /**
   * Attempts to post a Kluch with given content for a user with given username.
   * @param username author of given Kluch, cannot be null or empty
   * @param kluchText Kluch content, cannot be null or empty
   * @throws KluchPreviouslyPostedException if the last <code>kluchText</code> posted by this user is identical to this kluchText
   * @throws NullOrEmptyException if either <code>username</code> or <code>kluchText</code> are null or empty
   * @throws InvalidLengthException if <code>kluchText</code> is longer than allowed
   */
  public Kluch post(String username, String kluchText) throws KluchPreviouslyPostedException, NullOrEmptyException, InvalidLengthException {
    validateInput(username, kluchText);
    checkAlreadyPosted(username, kluchText);
    Kluch kluch = kluchAssembler.assembleKluch(username, kluchText);
    kluch = kluchDao.save(kluch);
    saveLastKluch(username, kluchText);
    log.info("User [{}] successfuly posted a Kluch with text [{}].", username,
        kluchText);
    return kluch;
  }

  private void checkAlreadyPosted(String username, String kluchText) throws KluchPreviouslyPostedException {
    String pastKluch = pastKluchs.get(username);
    if (kluchText.equals(pastKluch)) {
      throw new KluchPreviouslyPostedException(username);
    }
  }
  
  private void validateInput(String username, String kluchText) throws NullOrEmptyException {
  	validateUsername(username);
    validateKluch(kluchText);
  }
  
  private void validateUsername(String username) throws NullOrEmptyException {
    if (username == null || username.isEmpty()) {
      throw new NullOrEmptyException("Username");
    }
  }
  
  private void validateKluch(String kluchText) throws NullOrEmptyException, InvalidLengthException {
    if (kluchText == null || kluchText.isEmpty()) {
      throw new NullOrEmptyException("Kluch content");
    } 
    if (kluchText.length() > KLUCH_MAX_LENGTH) {
        throw new InvalidLengthException("Kluch", Adjective.LONG);
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
