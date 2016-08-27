package com.soze.hashtag.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soze.kluch.model.Kluch;

@Entity
@Table(name = "hashtags")
public class Hashtag {

  @Id
  private String text;
  @JsonIgnore
  @ManyToMany(mappedBy = "hashtags", fetch = FetchType.EAGER)
  private Set<Kluch> kluchs = new HashSet<>();
  
  public Hashtag() {
    
  }
  
  public Hashtag(String hashtagText) {
    this.text = hashtagText;
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
  
  @Override
  public int hashCode() {
    return Objects.hash(text);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Hashtag))
      return false;
    Hashtag other = (Hashtag) obj;
    if (text == null) {
      if (other.text != null)
        return false;
    } else if (!text.equals(other.text))
      return false;
    return true;
  }
  
}
