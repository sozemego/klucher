package com.soze.kluch.service;

import org.springframework.stereotype.Service;

@Service
public class ValidateKluchService {

  private static final int MAX_LENGTH = 250;

  public boolean validateKluch(String kluch) {
    if (kluch.length() > MAX_LENGTH) {
      return false;
    }
    return true;
  }

}
