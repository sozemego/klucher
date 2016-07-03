package com.soze.kluch.service;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.exceptions.AlreadyPostedException;
import com.soze.kluch.exceptions.InvalidKluchContentException;
import com.soze.kluch.model.Kluch;

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
   * input and throws appropriate exceptions for invalid input.
   * @param username author of given Kluch, cannot be null or empty
   * @param kluchText Kluch content, cannot be null or empty
   * @throws AlreadyPostedException if this author's last successful kluch's content is identical to this one
   * @throws InvalidKluchContentException if the content is too long or otherwise invalid
   * @throws IllegalArgumentException if either username or kluch content is null or empty
   */
  public void post(String username, String kluchText)
      throws AlreadyPostedException, InvalidKluchContentException, IllegalArgumentException {
    if (username == null || username.isEmpty()) {
      throw new IllegalArgumentException("Username cannot be empty or null.");
    }
    if (kluchText == null || kluchText.isEmpty()) {
      throw new IllegalArgumentException(
          "Kluch content cannot be empty or null.");
    }
    checkAlreadyPosted(username, kluchText);
    Kluch kluch = kluchAssembler.assembleKluch(username, kluchText);
    kluchDao.save(kluch);
    saveLastKluch(username, kluchText);
    log.info("User [{}] successfuly posted a Kluch with text [{}]", username,
        kluchText);
  }

  private void checkAlreadyPosted(String username, String kluchText)
      throws AlreadyPostedException {
    String pastKluch = pastKluchs.get(username);
    if (!kluchText.equals(pastKluch)) {
      throw new AlreadyPostedException("User [" + username
          + "] tried to post a Kluch with content identical to the previous Kluch ["
          + kluchText + "]");
    }
  }

  private void saveLastKluch(String username, String kluchText) {
    pastKluchs.put(username, kluchText);
  }

  public void deleteKluch(long id) {
    kluchDao.delete(id);
  }

  public void deleteAll(String username) {
    kluchDao.deleteByAuthor(username);
  }

}
