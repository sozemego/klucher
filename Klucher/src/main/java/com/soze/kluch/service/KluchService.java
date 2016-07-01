package com.soze.kluch.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.soze.kluch.dao.KluchDao;
import com.soze.kluch.model.Kluch;

@Service
public class KluchService {

  private final KluchDao kluchDao;
  private final ValidateKluchService validateKluchService;
  private final KluchAssembler kluchAssembler;
  private final Map<String, String> pastKluchs = new HashMap<>();
  
  @Autowired
  public KluchService(KluchDao kluchDao, ValidateKluchService validateKluchService, KluchAssembler kluchAssembler) {
    this.kluchDao = kluchDao;
    this.validateKluchService = validateKluchService;
    this.kluchAssembler = kluchAssembler;
  }
  
  public boolean post(String username, String kluchText) {
    boolean validKluch = validateKluchService.validateKluch(kluchText);
    if(!validKluch) {
      return false;
    }
    boolean alreadyPosted = alreadyPosted(username, kluchText);
    if(alreadyPosted) {
      return false;
    }
    Kluch kluch = kluchAssembler.assembleKluch(username, kluchText);
    kluchDao.save(kluch);
    saveLastKluch(username, kluchText);
    return true;
  }
  
  private boolean alreadyPosted(String username, String kluchText) {
    String pastKluch = pastKluchs.get(username);
    if(pastKluch == null) {
      return false;
    }
    if(pastKluch.equals(kluchText)) {
      return true;
    }
    return false;
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
