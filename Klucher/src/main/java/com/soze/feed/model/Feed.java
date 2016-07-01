package com.soze.feed.model;

import org.springframework.data.domain.Page;

import com.soze.kluch.model.Kluch;

public class Feed {

  private Page<Kluch> kluchs;

  public Feed() {

  }

  public void setKluchs(Page<Kluch> kluchs) {
    this.kluchs = kluchs;
  }

  public Page<Kluch> getKluchs() {
    return kluchs;
  }

}
