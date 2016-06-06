package com.soze.login.model;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "persistent_logins")
public class RememberMeEntity {

  @NotNull
  private String username;
  @Id
  @NotNull
  private String series;
  @NotNull
  private String token;
  @NotNull
  private Timestamp last_used;

  private RememberMeEntity() {
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getSeries() {
    return series;
  }

  public void setSeries(String series) {
    this.series = series;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Timestamp getLast_used() {
    return last_used;
  }

  public void setLast_used(Timestamp last_used) {
    this.last_used = last_used;
  }

}
