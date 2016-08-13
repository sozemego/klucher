package com.soze.hashtag.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;

import com.soze.kluch.model.Kluch;

@Entity
public class Hashtag {

  @Id
  private String text;
  @ManyToMany(fetch = FetchType.EAGER)
  private Set<Kluch> kluchs = new HashSet<>();
  
  public Hashtag() {
    
  }
  
  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Set<Kluch> getKluchs() {
    return kluchs;
  }

  public void setKluchs(Set<Kluch> kluchs) {
    this.kluchs = kluchs;
  }
  
}
