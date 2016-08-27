package com.soze.kluch.model;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.soze.hashtag.model.Hashtag;

@Entity
@Table(name = "kluchs")
public class Kluch {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  @NotNull
  private String author;
  @NotNull
  private String text;
  @NotNull
  private Timestamp timestamp;
  @JsonIgnore
  @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Set<Hashtag> hashtags = new HashSet<>();
  

  public Kluch() {

  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getText() {
    return text;
  }

  public void setText(String text) {
    this.text = text;
  }

  public Timestamp getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(Timestamp timestamp) {
    this.timestamp = timestamp;
  }
  
  public void setHashtags(Set<Hashtag> hashtags) {
    this.hashtags = hashtags;
  }
  
  public Set<Hashtag> getHashtags() {
    return hashtags;
  }

}
