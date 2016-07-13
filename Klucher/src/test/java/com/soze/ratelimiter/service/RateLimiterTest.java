package com.soze.ratelimiter.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

public class RateLimiterTest {
  
  @Test
  public void testOneInteraction() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    boolean successful = rateLimiter.interact(username);
    assertThat(successful, equalTo(true));
  }
  
  @Test
  public void testMoreInteractions() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    int maxInteractionsPerMinute = 30;
    for(int i = 0; i < maxInteractionsPerMinute - 1; i++) {
      rateLimiter.interact(username);
    }
    boolean successful = rateLimiter.interact(username);
    assertThat(successful, equalTo(true));
  }
  
  @Test
  public void testTooManyInteractions() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    int interactions = 60;
    for(int i = 0; i < interactions; i++) {
      rateLimiter.interact(username);
    }
    boolean successful = rateLimiter.interact(username);
    assertThat(successful, equalTo(false));
  }
  
  @Test
  public void testNullUsername() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = null;
    boolean successful = rateLimiter.interact(username);
    assertThat(successful, equalTo(false));
  }
  
  @Test
  public void testEmptyUsername() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "";
    boolean successful = rateLimiter.interact(username);
    assertThat(successful, equalTo(false));
  }
  
  @Test
  public void testTimePassed() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    int maxInteractionsPerMinute = 30;
    for(int i = 0; i < maxInteractionsPerMinute * 2; i++) {
      rateLimiter.interact(username);
    }
    try {
      Thread.sleep((60 * 1000) + (10 * 1000));
    } catch (Exception e) {
      fail("Waiting failed");
    }
    boolean successful = rateLimiter.interact(username);
    assertThat(successful, equalTo(true));
  }

}
