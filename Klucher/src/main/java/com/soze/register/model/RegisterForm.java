package com.soze.register.model;

import java.util.Objects;

public class RegisterForm {
  
  private String username;
  private String password;

  public RegisterForm() {
  }

  public RegisterForm(String username, String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password);
  }

  @Override
  public boolean equals(Object two) {
    if (two == null) {
      return false;
    }
    if (two == this) {
      return true;
    }
    RegisterForm second = (RegisterForm) two;
    return getUsername().equals(second.getUsername())
        && getPassword().equals(second.getPassword());
  }
}
