package com.soze.feed.model;

import java.util.List;

/**
 * Feed is a simple wrapper class for a {@link List} of your desired type.
 * This may change in the future.
 * @author sozek
 *
 */
public class Feed<T> {

  private List<T> elements;

  public Feed() {

  }
  
  public Feed(List<T> elements) {
    this.elements = elements;
  }

  public void setElements(List<T> elements) {
    this.elements = elements;
  }

  public List<T> getElements() {
    return elements;
  }

}
