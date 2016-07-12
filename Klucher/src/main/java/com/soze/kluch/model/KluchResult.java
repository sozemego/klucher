package com.soze.kluch.model;

import org.springframework.http.HttpStatus;

/**
 * Represents a result of an attempt to post a new Kluch. This object is meant
 * to be useful to the client. Defaults to a fully successful result.
 * 
 * @author sozek
 *
 */
public class KluchResult {

  private final String author;
  private final String kluchText;
  private String message = "Successfully posted a Kluch.";
  private boolean successful = true;
  private HttpStatus status = HttpStatus.OK;

  public KluchResult(String author, String kluchText) {
    this.author = author;
    this.kluchText = kluchText;
  }

  public String getMessage() {
    return message;
  }

  public boolean isSuccessful() {
    return successful;
  }

  public HttpStatus getStatus() {
    return status;
  }

  private void setMessage(String message) {
    this.message = message;
  }
  
  public void setResult(String message, HttpStatus status) {
    setMessage(message);
    setStatus(status);
  }

  private void setStatus(HttpStatus status) {
    this.status = status;
    if(status.is2xxSuccessful()) {
      successful = true;
    } else {
      successful = false;
    }
  }

  @Override
  public String toString() {
    return "Author: [" + author + "]. KluchText: [" + kluchText + "]. Message: [" + message + "]. Status code [" + status + "]. Successful [" + successful
        + "]";
  }

}
