package com.soze.feed.model;

import java.util.Collection;
import java.util.List;

/**
 * Feed is a simple wrapper class for a {@link List} of your desired type.
 * This may change in the future.
 * @author sozek
 *
 */
public class Feed<T> {

  private Collection<T> elements;

  public Feed() {

  }
  
  public Feed(Collection<T> elements) {
    this.elements = elements;
  }

  public void setElements(Collection<T> elements) {
    this.elements = elements;
  }

  public Collection<T> getElements() {
    return elements;
  }

}
