package com.soze.ratelimiter.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Ignore;
import org.junit.Test;
import org.springframework.http.HttpHeaders;

public class RateLimiterTest {

  @Test
  public void testOneInteraction() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    HttpHeaders headers = rateLimiter.interact(username);
    assertThat(headers.get("X-Rate-Limit-Limit").get(0), equalTo("60"));
    assertThat(headers.get("X-Rate-Limit-Remaining").get(0), equalTo("59"));
    assertThat(headers.get("X-Rate-Limit-Reset").get(0), equalTo("0"));
  }
  
  @Test
  public void testMoreInteractions() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    int maxInteractionsPerMinute = 30;
    for(int i = 0; i < maxInteractionsPerMinute - 1; i++) {
      rateLimiter.interact(username);
    }
    HttpHeaders headers = rateLimiter.interact(username);
    assertThat(headers.get("X-Rate-Limit-Limit").get(0), equalTo("60"));
    assertThat(headers.get("X-Rate-Limit-Remaining").get(0), equalTo("30"));
    assertThat(headers.get("X-Rate-Limit-Reset").get(0), equalTo("0"));
  }
  
  @Test
  public void testTooManyInteractions() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    int interactions = 60;
    for(int i = 0; i < interactions; i++) {
      rateLimiter.interact(username);
    }
    HttpHeaders headers = rateLimiter.interact(username);
    assertThat(headers.get("X-Rate-Limit-Limit").get(0), equalTo("60"));
    assertThat(headers.get("X-Rate-Limit-Remaining").get(0), equalTo("0"));
    assertThat(headers.get("X-Rate-Limit-Reset").get(0), equalTo("60"));
  }
  
  @Test
  @Ignore
  public void testTimePassed() {
    RateLimiter rateLimiter = new RateLimiter();
    String username = "user";
    int maxInteractionsPerMinute = 175;
    for(int i = 0; i < maxInteractionsPerMinute; i++) {
      rateLimiter.interact(username);
    }
    try {
      Thread.sleep((60 * 1000) + (10 * 1000));
    } catch (Exception e) {
      fail("Waiting failed");
    }
    HttpHeaders headers = rateLimiter.interact(username);
    assertThat(headers.get("X-Rate-Limit-Limit").get(0), equalTo("60"));
    assertThat(headers.get("X-Rate-Limit-Remaining").get(0), equalTo("59"));
    assertThat(headers.get("X-Rate-Limit-Reset").get(0), equalTo("0"));
  }

}
