package com.soze.ratelimiter.model;

import org.springframework.http.HttpMethod;

public class Interaction {

  private final String username;
  private final String endpoint;
  private final HttpMethod method;
  
  public Interaction(String username, String endpoint, HttpMethod method) {
    this.username = username;
    this.endpoint = endpoint;
    this.method = method;
  }

  public String getUsername() {
    return username;
  }

  public String getEndpoint() {
    return endpoint;
  }

  public HttpMethod getMethod() {
    return method;
  }

  // auto generated
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((endpoint == null) ? 0 : endpoint.hashCode());
    result = prime * result + ((method == null) ? 0 : method.hashCode());
    result = prime * result + ((username == null) ? 0 : username.hashCode());
    return result;
  }

  // auto generated
  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (!(obj instanceof Interaction))
      return false;
    Interaction other = (Interaction) obj;
    if (endpoint == null) {
      if (other.endpoint != null)
        return false;
    } else if (!endpoint.equals(other.endpoint))
      return false;
    if (method != other.method)
      return false;
    if (username == null) {
      if (other.username != null)
        return false;
    } else if (!username.equals(other.username))
      return false;
    return true;
  }

  
}
