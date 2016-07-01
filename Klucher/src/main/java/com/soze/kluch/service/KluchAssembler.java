package com.soze.kluch.service;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.soze.kluch.model.Kluch;

@Service
public class KluchAssembler {

  public Kluch assembleKluch(String author, String kluchText) {
    Kluch kluch = new Kluch();
    kluch.setAuthor(author);
    kluch.setText(kluchText);
    kluch.setTimestamp(new Timestamp(Instant.now().toEpochMilli()));
    return kluch;
  }
  
  
}
