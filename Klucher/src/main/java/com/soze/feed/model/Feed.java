package com.soze.feed.model;

import org.springframework.data.domain.Page;

import com.soze.kluch.model.Kluch;

/**
 * Feed is a simple wrapper class for a {@link Page} of Kluchs.
 * @author sozek
 *
 */
public class Feed {

  private Page<Kluch> kluchs;

  public Feed() {

  }
  
  public Feed(Page<Kluch> kluchs) {
    this.kluchs = kluchs;
  }

  public void setKluchs(Page<Kluch> kluchs) {
    this.kluchs = kluchs;
  }

  public Page<Kluch> getKluchs() {
    return kluchs;
  }

}
