package com.soze.user.model;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;

@Entity
public class UserRoles {

  @Id
  private String username;
  @NotNull
  private boolean user;
  @NotNull
  private boolean admin;
  
  public UserRoles() {
    
  }
  
  public UserRoles(String username, boolean user, boolean admin) {
    this.username = username;
    this.user = user;
    this.admin = admin;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public boolean isUser() {
    return user;
  }

  public void setUser(boolean user) {
    this.user = user;
  }

  public boolean isAdmin() {
    return admin;
  }

  public void setAdmin(boolean admin) {
    this.admin = admin;
  }
  
}
