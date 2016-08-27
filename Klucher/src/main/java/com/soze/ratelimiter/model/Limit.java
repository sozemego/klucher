package com.soze.ratelimiter.model;

import java.util.Objects;

import org.springframework.http.HttpMethod;

public class Limit {

  private final HttpMethod method;
  private final int limit;
  
  public Limit(HttpMethod method, int limit) {
    this.method = method;
    this.limit = limit;
  }
 
  public HttpMethod getMethod() {
    return method;
  }

  public int getLimit() {
    return limit;
  }

  @Override
  public int hashCode() {
    return Objects.hash(method);
  }
  
  @Override
  public boolean equals(Object two) {
    if(two == null) {
      return false;
    }
    if(this == two) {
      return true;
    }
    Limit second = (Limit) two;
    if(getMethod().equals(second.getMethod())) {
      return true;
    }
    return false;
  }
  
  
  
}
