package com.soze.user.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.validation.constraints.NotNull;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Entity
public class User implements UserDetails {

  @Id
  private String username;
  @NotNull
  private String hashedPassword;
  @OneToOne(cascade = {CascadeType.ALL })
  @NotNull
  private UserRoles userRoles;

  public User() {

  }

  public String getHashedPassword() {
    return hashedPassword;
  }

  public void setHashedPassword(String hashedPassword) {
    this.hashedPassword = hashedPassword;
  }

  public UserRoles getUserRoles() {
    return userRoles;
  }

  public void setUserRoles(UserRoles userRoles) {
    this.userRoles = userRoles;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    List<GrantedAuthority> list = new ArrayList<>();
    if (userRoles.isUser()) {
      list.add(new SimpleGrantedAuthority("ROLE_USER"));
    }
    if (userRoles.isAdmin()) {
      list.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }
    return list;
  }

  @Override
  public String getPassword() {
    return hashedPassword;
  }

  @Override
  public String getUsername() {
    return username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
  
  
  @Override
  public int hashCode() {
    return username.hashCode();
  }
  
  @Override
  public boolean equals(Object second) {
      if (second instanceof User) {
          return username.equals(((User) second).username);
      }
      return false;
  }

}
